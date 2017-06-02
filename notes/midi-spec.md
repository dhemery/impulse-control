# MIDI Specifications

## Channel Voice Messages
- 0x8n Note Off (note#, velocity)
- 0x9n Note On (note#, velocity)
- 0xAn Key Aftertouch (note#, pressure)
- 0xBn Control Change (controller#, value)
- 0xCn Program Change (program#)
- 0xDn Channel Aftertouch (pressure)
- 0xEn Pitch Bend (LSB, MSB)

## Channel Mode Messages
- 0xBn Selects Channel Mode (message#, value)
    - When controller number is 120 (0x78) or higher
- Message is determined by the value of data0
    - 120 (0x78) All Sound Off (0)
    - 121 (0x79) Reset All Controllers (0)
    - 122 (0x7a) Local Control (0: Local Control Off, 127: Local Control On)
    - 123 (0x7b) All Notes Off (0)
    - 124 (0x7c) Omni Mode Off (0)
    - 125 (0x7d) Omni Mode On (0)
    - 126 (0x7e) Mono Mode On (number of channels, where 0 is special)
    - 127 (0x7f) Poly Mode On (0)

## System Messages

### System Exclusive
- 0xF0 SOX (any number of bytes ending with 0xf7 EOX)
- 0xF7 EOX ()

### System Common
- 0xF1 MIDI Time Code Quarter Frame (special)
- 0xF2 Song Position Pointer (LSB, MSB)
- 0xF3 Song Select (song#)
- 0xF4 Undefined (?)
- 0xF5 Undefined (?)
- 0xF6 Tune Request ()

### System Real Time
- 0xF8 Timing Clock
- 0xF9 Undefined
- 0xFa Start
- 0xFb Continue 
- 0xFc Stop
- 0xFd Undefined
- 0xFe Active Sensing
- 0xFf System Reset

