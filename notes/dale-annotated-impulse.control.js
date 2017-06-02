loadAPI(1);

host.defineController("Novation", "Impulse 25", "1.0", "3B1F8670-2433-11E2-81C1-0800200C9A66");
host.defineMidiPorts(2, 1);
host.addDeviceNameBasedDiscoveryPair(["Impulse", "MIDIIN2 (Impulse)"], ["Impulse"]);
for (var i = 1; i < 9; i++) {
    var name = i.toString() + "- Impulse";
    host.addDeviceNameBasedDiscoveryPair([name], [name]);
    host.addDeviceNameBasedDiscoveryPair(["Impulse MIDI " + i.toString()], ["Impulse MIDI " + i.toString()]);
}

var CC = {
    PLAY: 30,
    STOP: 29,
    RECORD: 32,
    REWIND: 27,
    FORWARD: 28,
    LOOP: 31,
    PAGE_UP: 11,
    PAGE_DOWN: 12,
    SLIDER: 8,
    MIXER: 9,
    PLUGIN: 10,
    MIDI: 11,
    NEXT_TRACK: 37,
    PREV_TRACK: 38,
    SHIFT: 39
};

var NOTE = {
    PAD1: 67,
    PAD2: 69,
    PAD3: 71,
    PAD4: 72,
    PAD5: 60,
    PAD6: 62,
    PAD7: 64,
    PAD8: 65
};

var CLIP = {
    PAD1: 60,
    PAD2: 61,
    PAD3: 62,
    PAD4: 63,
    PAD5: 64,
    PAD6: 65,
    PAD7: 66,
    PAD8: 67
};

var PAD = {
    P1: 40,
    P2: 41,
    P3: 42,
    P4: 43,
    P5: 36,
    P6: 37,
    P7: 38,
    P8: 39
}

var color = {
    YELLOW: 55,
    LIGHT_GREEN: 49,
    FULL_GREEN: 48,
    DARK_GREEN: 84,
    ORANGE: 87,
    RED: 67,
    RED_BLINK: 74,
    OFF: 64
};

var SYSEX_HEADER = "F0 00 20 29 67";
var NUM_COLUMS = 8;
var isShiftPressed = false;
var isPlay = false;
var isLoopPressed = false;

var isPlaying = initArray(0, 8);
var isQueued = initArray(0, 8);
var isRecording = initArray(0, 8);
var hasContent = initArray(0, 8);
var arm = initArray(0, 2);
var macroIndex = 0;
var playPads = 145;
var padShift = 0;

function init() {
    host.getMidiInPort(0).setMidiCallback(onMidi);
    host.getMidiInPort(1).setMidiCallback(onMidi);
    host.getMidiInPort(0).setSysexCallback(onSysex);
    host.getMidiOutPort(0).setShouldSendMidiBeatClock(true);
    impulseKeys = host.getMidiInPort(0).createNoteInput("Impulse Keyboard", "80????", "90????", "B?01??", "B040??", "D0????", "E0????"); // "B040??"-> stops the 5th pad to work
    impulseKeys = host.getMidiInPort(1).createNoteInput("Impulse Keyboard2", "80????", "90????", "B001??", "B040??", "D0????", "E0????"); // "B040??"-> stops the 5th pad to work
    //impulseKeys = host.getMidiInPort(0).createNoteInput("Impulse Keyboard", "80????", "90????", "B001??", "D0????", "E0????");
    impulsePads = host.getMidiInPort(0).createNoteInput("Impulse Pads", "81????", "91????", "D1????", "E1????");

    // Set the Impulse to the needed Mode:
    sendSysex(SYSEX_HEADER + "06 01 01 01 F7");
    sendSysex(SYSEX_HEADER + "07 19 F7");
    sendChannelController(60, 48 + 5, 0);
    sendChannelController(0xb1, 10, 127);
    // sendSysex(SYSEX_HEADER + "08" + "20 62 69 74 77 69 67 20 20 F7"); //bitwig string to display
    // sendSysex("F0 00 20 29 67 08 31 2D 41 75 64 69 6F 20 20 20 20 20 20 20 20 20 F7"); // displaytest?

    // /////////// Host
    transport = host.createTransport();
    transport.addIsPlayingObserver(function(on) {
        isPlay = on;
    });

    trackBank = host.createTrackBank(8, 2, 0);
    cursorTrack = host.createCursorTrack(2, 0);
    cursorDevice = host.createCursorDevice();
    clipGrid = host.createTrackBank(2, 0, 4);
    primaryInstrument = cursorTrack.getPrimaryInstrument();

    clipGrid.getTrack(0).getClipLauncherSlots().setIndication(true);
    clipGrid.getTrack(1).getClipLauncherSlots().setIndication(true);

    for (var p = 0; p < 8; p++) {
        var parameter = cursorDevice.getParameter(p);
        macro = primaryInstrument.getMacro(p);
        macro.getAmount().setIndication(true);
        parameter.setLabel("P" + (p + 1));
        macro.getAmount().addValueDisplayObserver(3, "", getObserverIndex(p, function(index, value) {
            pluginPage.valueToDisplay(value);
        }));
    }

    for (var t = 0; t < 2; t++) {
        var grid = clipGrid.getTrack(t);
        var clipLauncher = grid.getClipLauncher();
        clipLauncher.addHasContentObserver(getGridObserverFunc(t, hasContent));
        clipLauncher.addIsPlayingObserver(getGridObserverFunc(t, isPlaying));
        clipLauncher.addIsQueuedObserver(getGridObserverFunc(t, isQueued));
        clipLauncher.addIsRecordingObserver(getGridObserverFunc(t, isRecording));
    }
    cursorTrack.addNameObserver(16, "", function(text) {
        sendSysex(SYSEX_HEADER + "08" + text.toHex(text.length) + " F7");
    });

    initImpulse();
}

function exit() {}

function onMidi(status, data1, data2) {
    printMidi(status, data1, data2);

    if (isChannelController(status)) {
        if (status == 0xb1) {

            if (data1 >= 0 && data1 <= 7) { // Rotary Encoders
                var relativeRange = isLoopPressed ? 500 : 100;
                var encoderId = data1;
                impulseActiveEncoderPage.onEncoder(encoderId, data2 - 64, relativeRange);
            } else if (data1 == CC.PLUGIN) {
                setEncoderMode(pluginPage);
                host.showPopupNotification("Device Page");
            } else if (data1 == CC.MIXER) {
                setEncoderMode(mixerPage);
                host.showPopupNotification("Mixer Page")
            } else if (data1 == CC.PAGE_UP) {
                isShiftPressed = false;
                impulseActiveEncoderPage.setIndications("notpressed");
                impulseActiveEncoderPage.pageUp();
            } else if (data1 == CC.PAGE_DOWN) {
                isShiftPressed = false;
                impulseActiveEncoderPage.setIndications("notpressed");
                impulseActiveEncoderPage.pageDown();
            }

        }
        if (status == 0xb0) {
            var cc = data1;
            var val = data2;
            var pressed = cc - CLIP.PAD1;
            if (cc >= CLIP.PAD1 && cc < CLIP.PAD1 + 4) {
                isLoopPressed ? clipGrid.launchScene(pressed) : clipGrid.getTrack(0).getClipLauncher().launch(pressed);
            } else if (cc >= CLIP.PAD1 + 4 && cc < CLIP.PAD1 + 8) {
                isLoopPressed ? clipGrid.launchScene(pressed - 4) : clipGrid.getTrack(1).getClipLauncher().launch(pressed - 4);
            }

            switch (cc) {
                case CC.SLIDER:
                    cursorTrack.getVolume().set(val, 128);
                    break;
                case CC.SHIFT:
                    isShiftPressed = val > 0;
                    isShiftPressed ? impulseActiveEncoderPage.setIndications("pressed") : impulseActiveEncoderPage.setIndications("notpressed");
                    break;
                case CC.LOOP:
                    isLoopPressed = val > 0;

                    if (isShiftPressed && isLoopPressed) transport.toggleLoop();
                    break;
            }

            if (val > 0) { // ignore button release

                switch (cc) {
                    case CC.PLAY:
                        isLoopPressed ? transport.returnToArrangement() : transport.play();
                        break;

                    case CC.STOP:
                        isLoopPressed ? transport.resetAutomationOverrides() : transport.stop();
                        break;

                    case CC.RECORD:
                        isLoopPressed ? cursorTrack.getArm().toggle() : transport.record();
                        break;

                    case CC.REWIND:
                        impulseActiveEncoderPage.rewindAction();
                        break;

                    case CC.FORWARD:
                        impulseActiveEncoderPage.forwardAction();
                        break;

                    case CC.PREV_TRACK:
                        isShiftPressed = false;
                        impulseActiveEncoderPage.setIndications("notpressed");
                        cursorTrack.selectPrevious();
                        break;

                    case CC.NEXT_TRACK:
                        isShiftPressed = false;
                        impulseActiveEncoderPage.setIndications("notpressed");
                        cursorTrack.selectNext();
                        break;
                }
            }
        }
    }

}

function onSysex(data) {
    printSysex(data);
}

function EncoderObject() {
    this.textBuffer = [];

    for (var i = 0; i < NUM_COLUMS; i++) {
        this.textBuffer[i] = ' ';
    }
}

EncoderObject.prototype.onEncoder = function(index, diff, range) {
    this.getPathToObject(index).inc(diff, range);
};

var pluginPage = new EncoderObject();

pluginPage.getPathToObject = function(index) {
    return isShiftPressed ? cursorDevice.getParameter(index) : primaryInstrument.getMacro(index).getAmount();
};

pluginPage.rewindAction = function() {
    isShiftPressed ? cursorDevice.previousParameterPage() : isLoopPressed ? clipGrid.scrollTracksUp() : clipGrid.scrollScenesUp();
}

pluginPage.forwardAction = function() {
    isShiftPressed ? cursorDevice.nextParameterPage() : isLoopPressed ? clipGrid.scrollTracksDown() : clipGrid.scrollScenesDown();
}
pluginPage.pageUp = function() {
    cursorDevice.selectPrevious();
}

pluginPage.pageDown = function() {
    cursorDevice.selectNext();
}

pluginPage.setIndications = function(isShift) {
    switch (isShift) {
        case "pressed":
            for (var p = 0; p < 8; p++) {
                primaryInstrument.getMacro(p).getAmount().setIndication(false);
                track = trackBank.getTrack(p);
                cursorDevice.getParameter(p).setIndication(true);
                track.getVolume().setIndication(false);
                track.getPan().setIndication(false);
            }
            break;
        case "notpressed":
            for (var p = 0; p < 8; p++) {
                primaryInstrument.getMacro(p).getAmount().setIndication(true);
                track = trackBank.getTrack(p);
                cursorDevice.getParameter(p).setIndication(false);
                track.getVolume().setIndication(false);
                track.getPan().setIndication(false);
            }
            break;
    }
}

// //////////////////// work in progress ///////////////////////
pluginPage.macroLabelBuffer = function(index, text) {
    var param = index * NUM_COLUMS;
    var forcedText = text.forceLength(NUM_COLUMS);
    for (var i = 0; i < NUM_COLUMS; i++) {
        this.textBuffer[i + param] = forcedText[i];
    }
    pluginPage.sendToDisplay();
}
pluginPage.sendToDisplay = function() {
    var tb = macroIndex * NUM_COLUMS;
    var text = "";

    for (var i = 0; i < NUM_COLUMS; i++) {
        text += this.textBuffer[tb + i];
    }
    sendSysex(SYSEX_HEADER + "08" + this.textBuffer[text].toHex(NUM_COLUMS) + "F7");
}

pluginPage.valueToDisplay = function(value) {
    if (parseInt(value) < 10) {
        sendSysex(SYSEX_HEADER + "09 20 20" + value.toHex(1) + "F7");
    } else if (parseInt(value) >= 10 && parseInt(value) < 100) {
        sendSysex(SYSEX_HEADER + "09 20" + value.toHex(2) + "F7");
    } else {
        sendSysex(SYSEX_HEADER + "09" + value.toHex(3) + "F7");
    }
}
// ///////////////////////////////////////////////////////////////////

var mixerPage = new EncoderObject();

mixerPage.getPathToObject = function(index) {
    if (isShiftPressed) {
        return trackBank.getTrack(index).getPan();
    } else {
        return trackBank.getTrack(index).getVolume();
    }
};

mixerPage.rewindAction = function() {
    isShiftPressed ? transport.rewind() : isLoopPressed ? clipGrid.scrollTracksUp() : clipGrid.scrollScenesUp();
}

mixerPage.forwardAction = function() {
    isShiftPressed ? transport.fastForward() : isLoopPressed ? clipGrid.scrollTracksDown() : clipGrid.scrollScenesDown();
}

mixerPage.pageUp = function() {
    trackBank.scrollTracksPageUp();

}

mixerPage.pageDown = function() {
    trackBank.scrollTracksPageDown();

}
mixerPage.setIndications = function(isShift) {
    switch (isShift) {
        case "pressed":
            for (var p = 0; p < 8; p++) {
                primaryInstrument.getMacro(p).getAmount().setIndication(false);
                track = trackBank.getTrack(p);
                track.getVolume().setIndication(false);
                track.getPan().setIndication(true);
                cursorDevice.getParameter(p).setIndication(false);
            }
            break;
        case "notpressed":
            for (var p = 0; p < 8; p++) {
                macro = primaryInstrument.getMacro(p).getAmount();
                track = trackBank.getTrack(p);
                track.getVolume().setIndication(true);
                track.getPan().setIndication(false);
                macro.setIndication(false);
                cursorDevice.getParameter(p).setIndication(false);
            }
            break;
    }

}

var impulseActiveEncoderPage = pluginPage;

function setEncoderMode(page) {
    impulseActiveEncoderPage = page;
    impulseActiveEncoderPage.setIndications("notpressed");
}

function valueToDisplay(value) {
    if (parseInt(value) < 10) {
        sendSysex(SYSEX_HEADER + "09 20 20" + value.toHex(1) + "F7");
    } else if (parseInt(value) >= 10 && parseInt(value) < 100) {
        sendSysex(SYSEX_HEADER + "09 20" + value.toHex(2) + "F7");
    } else {
        sendSysex(SYSEX_HEADER + "09" + value.toHex(3) + "F7");
    }
}

function getObserverIndex(index, f) {
    macroIndex = index;
    return function(value) {
        f(index, value);
    };
}

function getGridObserverFunc(track, varToStore) {
    return function(scene, value) {
        var index = track * 4 + (scene + 1);
        varToStore[index] = value;
        var state = isRecording[index] ? color.RED : isPlaying[index] ? color.FULL_GREEN : isQueued[index] ? color.LIGHT_GREEN : hasContent[index] ? color.YELLOW : arm[track] ? color.ORANGE : color.OFF;
        setClipGridLEDs(index, state);
        // gridPage.updateTrackValue(track);
    };
}

function setClipGridLEDs(button, state) {
    sendNoteOn(0xB0, 59 + button, state);
}

function initImpulse() {
    impulseActiveEncoderPage.setIndications("notpressed");
}
