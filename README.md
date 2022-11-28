# GenCaster Quark

Live coding client for the GenCaster system.

## Installation

Install the GenCaster client as a Quark by evaluating the following statement in the SuperCollider IDE.

```supercollider
Quarks.install("https://github.com/GenCaster/gencaster-quark.git");

// recompile interpreter to make new classes available
thisProcess.platform.recompile;
```

## Usage

The client allows you to access the SuperCollider instances which are generating the audio stream.
They allow you to execute *sclang* code.

> Please do not overwrite the variable `g` as this is used for internal processing.

### Client setup

```supercollider
// credentials are already setup for the demo
c = GenCaster();
```

### Send instruction to specific stream

Send e.g. to stream `4`

```supercollider
c.activate(4);

// this will now be executed on stream #4
Ndef(\foo, {SinOsc.ar*0.2!2}).play;

// stop remote evaluation
c.clear;
```

### Send instruction to multiple streams

Send e.g. to stream `3`, `5` and `7`

```supercollider
c.activate(3, 5, 7);

// this will now be executed on stream 3, 5 and 7
Ndef(\foo, {SinOsc.ar(LFDNoise3.kr(4.0!2).exprange(400, 450))}).play;

// stop remote evaluation
c.clear;
```

### Send instruction to all streams

Send to all available streams via `broadcast`

```supercollider
c.broadcast;

Ndef.clear;

// stop remote evaluation
c.clear;
```

### Speak on stream

Speak on stream `5`

```supercollider
c[5].speak("Jeder für sich und Gott gegen alle");
```
