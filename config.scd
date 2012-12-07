
~passive_config = (
	preset_path: Platform.userAppSupportDir +/+ "passive/presets/", // directory is created automatically
	wavetable_paths: [
		// put here path to directory containing wavetable files (simple short .wav)
		"~/Musique/archwavetable/Architecture Waveforms 2010 Wav24/Architecture Waveforms 2010 Wav24",
	],

	macro_midi_cc: [
		16,17,18,19, 20,21,22,23
	],
	master_volume_cc: 24,

	wavetable_buffer_size: 2048,
	lfo_buffer_size: 2048,
	performer_buffer_size: 4096,

	init_jack: { arg self;
		// function called when creating midi responders
		"jack_connect 'A-PRO:midi/playback_2' 'SuperCollider:midi/capture_1'".unixCmd;
	},
)
