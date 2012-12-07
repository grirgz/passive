plop

Passive is an Instr plus a GUI which imitate a well known VST. 
You can refer to the original VST manual if you want to understand more the usage of passive

Dependencies (quarks)
=====================

- ixiViews
- cruciallib
- Feedback

INSTALL
=======

- Copy files in Extensions folder to your Supercollider Extensions folder
- Copy preset directory in the path specified in config.scd
- Edit main.scd and change the path to reflect to true location of passive files
- Edit config.scd, you can set CC controller numbers, path of wavetable files, etc

getting started:

- Edit passive.scd and follow instructions

Warning
=======

This software is in early development and not well tested.
There is maybe some bugs which will cause sound explosion, be ready to put volume down
If you think your changes in the GUI are not taken in account, put on and off a module (an osc for example) and the synthdef will be rebuild

GUI Usage
=========

Range knobs: 
	- click and hold, move horizontaly to change value. If your mouse is verticaly far from the knob, the value will change faster
	- keyboard shortcuts:
		- keypad enter: edit value (enter again to update value, and escape to close edit window)
		- x: set to minimum
		- c: set to center
		- v: set to maximum

Range slots (slots under the knobs):
	- drag here a modulator (the drag source is at right of the panel tabs (Env1, LFO1, etc) or macro knobs)
	- left click and hold and move horizontaly to change the range
	- middle click delete the modulator
	- right click mute the modulator

Custom wavetable:
	- choose "custom" in the curve menu at the top of oscillators frames or in the LFO modulators,
		a window will open to let you select your curves
	- you can add curve functions in paramdata.scd in the Event stored in ~curvebank
	- you can add path to wavetable samples in config.scd

Limitations
===========

- I don't own the original VST, so passive output will certainly sound different
- Env4 is hard wired to the Synth master amp enveloppe
- The SynthDef names are \passive_++preset_name and \passive_fx_++preset_name, be sure to use different preset names to play
	differents Passive configurations in parrallel
- Oscillators and modulators are created and freed with each note, only FX can play continuously.
- Not implemented:
	- LFO phase
	- Noise color
	- Bypass modulation
	- Amp modulation
	- Equalizer
	- Save/load modulator presets
	- Sidechain modulation
	- AfterTouch
- Bend and formant osc mode don't works as expected
- Env modulator don't have special morph sustain segment
- Can't limit polyphony

Licence
=======

GNU GPL 2

Author
======

Grirgz 
https://github.com/grirgz/passive
