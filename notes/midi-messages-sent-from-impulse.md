# MIDI messages sent from Impulse




## Notes

### Keys
- 0xD0 Channel Pressure (pressure, 0)

### Pads
- 0xD0 Channel Pressure (pressure, 0)




## Numbers

### Encoder 1..9
- When Encoder Mode is MIDI
    - 0xB1 ID(15..1c) 00..7f
- When Encoder Mode is Plugin or Mixer
    - 0xB1 ID(00..07) 3f,41 (decrement, increment)

### Fader 1..9
- When Fader Mode is Mixer
    - 0xB0 ID(00..08) 0..7f
- When Fader Mode is MIDI
    - 0xB0 ID(29..30) 0..7f

### Pitch Bend
- 0xE0 00 0..7f (data0 goes to 7f when data1 goes to 7f)

### Mod Wheel
- 0xB2 01 0..7f
- Why channel 2?
    - Guess: To avoid collisions with Plugin/Mixer encoder 2 and Mixer fader 2




## Buttons

### Fader Mode Buttons
- MIDI
    - 0xB0 21 0
- Mixer
    - 0xB0 22 0,1 (Sends Solo/Mute Button mode message)

### Solo/Mute Mode Buttons
- Active only when Fader mode is Mixer
- 0xB0 22 0,1 (lit/solo, dim/mute)
- See Fader Mixer mode, which also sends the Solo/Mute mode message

### Encoder Mode Buttons
- MIDI
    - 0xB1 08 0
- Plugin
    - 0xB1 0a 0
- Mixer
    - press Encoder MIDI and Encoder Plugin together
    - 0xB1 09 0

### Solo/Mute Button 1..9
- When Fader Mode is Mixer
    - 0xB0 ID(09..11) 0,1 (release, press)
    - Sends a message on press (value=1), and another on release (value=0)
- When Fader Mode is MIDI
    - 0xB0 ID(33..3b) 0,7f (dim, lit)
    - Toggles with each press

### Transport
- All Buttons send the same messages
    - 0xB0 ID 1,0 (release, press)
- IDs
    - 1b Rewind
    - 1c Forward
    - 1d Stop
    - 1e Play
    - 1f Loop
    - 20 Record

### Shift Button
- Sends two messages with each press, and two with each release
- 0xB0 27 0,1 (release, press)
- 0xB1 0d 0,1 (release, press)

### Other Buttons
- TBD


## Channels

### Channel 0
- Note On/Off
- Channel Aftertouch
- Pitch Bend
- Fader Mode
- Fader Change
- Mute/Solo Mode Button
- Mute/Solo Button
- Transport Buttons
- Shift Button (also channel 1)

### Channel 1
- Shift Button (also channel 0)
- Encoder Mode
- Encoder Change

### Channel 2
- Mod Wheel