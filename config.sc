
~passive_config = (
	wavetable_paths: [
		"~/Musique/archwavetable/Architecture Waveforms 2010 Wav24/Architecture Waveforms 2010 Wav24".standardizePath,
		"~/Musique/archwavetable/Architecture Waveforms 2010 Wav24/Architecture Waveforms 2010 Wav24/Add - BinAdd Fib".standardizePath,
		"~/Musique/arcijijhwavetable/Architecture Waveforms 2010 Wav24/Architecture Waveforms 2010 Wav24/Add - BinAdd Fib".standardizePath,
	],
	macro_midi_cc: [
		16,17,18,19, 20,21,22,23
	],
	master_volume_cc: 24,
	preset_path: Platform.userAppSupportDir +/+ "passive/presets/",
	wavetable_buffer_size: 2048,
	lfo_buffer_size: 2048,
	performer_buffer_size: 4096,
	init_jack: { arg self;
		"jack_connect 'A-PRO:midi/playback_2' 'SuperCollider:midi/capture_1'".unixCmd;
	},
)
