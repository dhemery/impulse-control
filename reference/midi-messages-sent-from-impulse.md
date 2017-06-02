# MIDI messages sent from Impulse

## Mode Switches

### SHIFT
- Sends two messages with each press/release
- CC0 27 0,1 (release, press)
- CC1 0d 0,1 (release, press)

### Fader Mode
- MIDI
    - CC0 21 0
- Mixer
    - CC0 22 0,1 (Sends Solo/Mute Button mode message)

### Solo/Mute Button Mode
- Active only when Fader mode is Mixer
- CC0 22 0,1 (lit/solo, dim/mute)
- See Fader Mixer mode, which also sends the Solo/Mute mode message

### Encoder Mode
- MIDI
    - CC1 08 0
- Plugin
    - CC1 0a 0
- Mixer
    - press Encoder MIDI and Encoder Plugin together
    - CC1 09 0

## Fader 1...9
- When Fader Mode is Mixer
    - CC0 00..08 0...7f
- When Fader Mode is MIDI
    - CC0 29...30 0...7f

## Solo/Mute Button 1...9
- When Fader Mode is Mixer
    - CC0 09...11 0,1 (release, press)
    - Sends a message on press, and another on release
- When Fader Mode is MIDI
    - CC0 33...3b 0,7f (dim, lit)
    - Toggles with each press

## Encoder 1...9
- When Encoder Mode is MIDI
    - CC1 15...1c 0...127 (normal CC value) 
- When Encoder Mode is Plugin or Mixer
    - CC1 00...07 3f,41 (decrement, increment) 


<!--
Pads
CHANNEL PRESSURE 0 data0 data1
I masked it out note on/off, so I'm not seeing it in my debug printouts

Transport
Back: CC0 1b 0,1 (release, press)
Forw: CC0 1c 0,1 (release, press)
Stop: CC0 1d 0,1 (release, press)
Play: CC0 1e 0,1 (release, press)
Loop: CC0 1f 0,1 (release, press)
Rec: CC0 20 0,1 (release, press)

Wheels

Pitch: e0 00 0...7f (data0 goes to 7f when data1 goes to 7f)
Mod: CC2 01 0...7f
-->