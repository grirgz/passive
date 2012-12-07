(
	// change to the path where main.sc is (don't forget to change path in main.scd too)
	"~/code/sc/passive/main.scd".standardizePath.load; // load code files
)

(
~new_passive.(); // start a new passive session and store it in ~passive (and close the old one if exists)
~passive.load_preset_by_uname("default"); // load a preset (here, the default preset)
~midiresp = ~passive.make_midi_responder; // start the midi notes and cc responders
~passive.make_gui; // show the gui
)

~passive.destructor; // destroy and free all ressources of this session

(
// example of using the synthdef in code
~piano = ~passive.get_piano;
Task {
	
	loop {

		~sy = ~piano.(300, 0.7); // play a 300Hz note with 0.7 as velocity
		2.wait;
		~sy.release_node;
		1.wait;
	}
}.play
)

/// accessing the internals

~passive.build_synthdef; // rebuild the synthdef
~preset = ~passive.save_preset // save preset in a variable
~passive.load_preset(~preset) // load it
~passive.save_current_preset_as_uname(\mypreset); // save a preset in a file
~passive.get_arg(\modulator1_rate) // get a parameter controller
~passive.synthdef_args.getPairs // get arguments used when calling Synth
~passive.synthdef_ns_args // get special arguments (used to build the SynthDef)
~passive.synthdef_name_suffix = "mysuffix" // the synthdef name will use this suffix (\passive_mysuffix and \passive_fx_mysuffix)

