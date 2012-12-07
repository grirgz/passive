~curvebank = (

//	saw4: { arg x; // bugged
//		var y;
//		y = sin(sqrt(x)/(x*2)).linlin(0,1,0,1);
//		y;
//	},
	name: "Builtin curves",

	////////////// methods

	get_keys: { arg self;
		var keys;
		//self.debug("curvebank: get_keys");
		keys = self.keys;
		keys = keys - Set[\get_keys, \known, \name];
		keys.asArray.sort;
	},
	
	//////////////// curves

	expsin: { arg x;
		var y;
		x = x % 1;
		y = sin(exp(x*2)*sin(x));
		y;
	},
	saw1: { arg x;
		var y;
		y = x % 1;
		y*2-1;
	},
	square1: { arg x;
		var y;
		x = x % 1;
		y = if(x<0.5) { -1 } { 1 };
		y;
	},
	triangle1: { arg x;
		var y;
		x = x % 1;
		y = if(x<0.5) { 2*x } { 2*(1-x) };
		y*2-1;
	},
	sin1: { arg x;
		var y;
		y = sin(x*2pi);
		y;
	},
	truesin1: { arg x;
		var y;
		y = sin(x*2pi);
		y;
	},
	sin2: { arg x;
		var y;
		y = sin(x*2pi*2);
		y;
	},
	sin4: { arg x;
		var y;
		y = sin(x*2pi*4);
		y;
	},

	// performer curves

	line1: { arg x;
		var y;
		y = x;
		y*2-1;
	},
	negline1: { arg x;
		var y;
		y = 1-x;
		y*2-1;
	},
	exp1: { arg x;
		var y;
		x = x % 1;
		y = x**2;
		y = y*2-1;
		y;
	},
	negexp1: { arg x;
		var y;
		x = x % 1;
		x = 1 - x;
		y = x**2;
		y = y*2-1;
		y;
	},
	exp2: { arg x;
		var y;
		x = x % 1;
		x = 1 - x;
		y = x**2;
		y = 0-y+1;
		y = y*2-1;
	},
	negexp2: { arg x;
		var y;
		x = x % 1;
		y = x**2;
		y = 0-y+1;
		y = y*2-1;
	},
	demisin2: { arg x;
		var y;
		x = x % 1;
		x = 1-x;
		y = sin(x*pi+(pi/2));
	},
	negdemisin2: { arg x;
		var y;
		x = x % 1;
		y = sin(x*pi+(pi/2));
	},
	unit: { arg x;
		1
	},
	negsquare1: { arg x;
		var y;
		x = x % 1;
		y = if(x<0.5) { 1 } { -1 };
		y;
	},
	demisin: { arg x;
		var y;
		x = x % 1;
		y = sin(x*pi);
		y = y*2-1;
	},

	pic1: { arg x;
		var y;
		var xx;
		var d = 1;
		xx = x % (1/d);
		xx = xx * d;
		xx = 1 - xx;
		y = xx ** 1.5 * (1+(sin(x*d*pi*0.7)/12));
		y = y*2-1;
	},
	pic2: { arg x;
		var y;
		var xx;
		var d = 2;
		xx = x % (1/d);
		xx = xx * d;
		xx = 1 - xx;
		y = xx ** 1.5 * (1+(sin(x*d*pi*0.7)/12));
		y = y*2-1;
	},
	pic4: { arg x;
		var y;
		var xx;
		var d = 4;
		xx = x % (1/d);
		xx = xx * d;
		xx = 1 - xx;
		y = xx ** 1.5 * (1+(sin(x*d*pi*0.7)/12));
		y = y*2-1;
	},

);
~curvebank.known = false; // FIXME: should be "know", is it required ? 

~passive_specs = (
	// (minval, maxval, warp, step, default, units)
	wt_pos: ControlSpec(0, 1, \lin, 0, 0),
	velocity: \unipolar.asSpec,
	ktr: \unipolar.asSpec,
	pitch: ControlSpec(-64,64, \lin, 0, 0, "midi"),
	rate: \widefreq.asSpec,
	glidefade: \unipolar.asSpec,
	env: \attack.asSpec,
	boost: ControlSpec(-500, 100, 'lin', 0, 0),
	amp: ControlSpec(0, 1, 'amp', 0, 0.1, ""),
	wideamp: ControlSpec(0, 6, 'amp', 0, 0.1, ""),
	crush: ControlSpec(1, 31, 'lin', 1, 1, ""),
	smalldelay: ControlSpec(0, 0.02, 'lin', 0, 0.001, ""),
	envamp: ControlSpec(0, 1, 'amp', 0, 1, "")
);

~passive_specs_steps = (
	// (minval, maxval, warp, step, default, units)
	pitch: (smallstep:1),
);


~make_module_kinds = {
	var module_kinds;
	var specs = ~passive_specs;
	var steps = ~passive_specs_steps;

	module_kinds = (
		filter: [
			(
				name: "LPF",
				uname: \lpf,
				args: ["Cutoff"],
				smallstep: [steps[\pitch].smallstep],
				specs: [specs[\pitch]]
			),
			(
				name: "RLPF",
				uname: \rlpf,
				args: ["Cutoff", "Resonance"],
				smallstep: [steps[\pitch].smallstep],
				specs: [specs[\pitch], \rq]
			),
			(
				name: "HPF",
				uname: \hpf,
				args: ["Cutoff"],
				specs: [specs[\pitch]]
			),
			(
				name: "RHPF",
				uname: \rhpf,
				args: ["Cutoff", "Resonance"],
				specs: [specs[\pitch], \rq]
			),
			(
				name: "BPF",
				uname: \bpf,
				args: ["Cutoff", "Resonance"],
				specs: [specs[\pitch], \rq]
			),
			(
				name: "Comb",
				uname: \comb,
				args: ["Max Delay", "Delay", "Decay"],
				specs: [\delay, \delay, \decay]
			),
		],
		noise: [
			(
				name: "White noise",
				uname: \white,
				args: ["Cutoff"],
				specs: [\freq]
			),
			(
				name: "Pink noise",
				uname: \pink,
				args: ["Cutoff"],
				specs: [\freq]
			),
			(
				name: "Brown noise",
				uname: \brown,
				args: ["Cutoff"],
				specs: [\freq]
			)
		],
		insert: [
			(
				name: "Freqshift",
				uname: \freqshift,
				args: ["Wet/Dry", "Shift"],
				specs: [\unipolar, specs[\pitch]]
			),
			(
				name: "Delay",
				uname: \simpledelay,
				args: ["Wet/Dry", "Delay"],
				specs: [\unipolar, \delay]
			),
			(
				name: "Hold",
				uname: \samplehold,
				args: ["Wet/Dry", "Pitch"],
				specs: [\unipolar, specs[\pitch]]
			),
			(
				name: "Bitcrusher",
				uname: \bitcrusher,
				args: ["Wet/Dry", "Crush"],
				specs: [\unipolar, specs[\crush]]
			),
			(
				name: "Filter",
				uname: \simplefilter,
				args: ["HP freq", "LP freq"],
				specs: [specs[\pitch], specs[\pitch]]
			),
			(
				name: "SineShaper",
				uname: \sinshaper,
				args: ["Wet/Dry", "Drive"],
				specs: [\unipolar, \unipolar]
			),
			(
				name: "ParaShaper",
				uname: \parashaper,
				args: ["Wet/Dry", "Drive"],
				specs: [\unipolar, \unipolar]
			),
			(
				name: "Hard clipper",
				uname: \hardclipper,
				args: ["Wet/Dry", "Drive"],
				specs: [\unipolar, \unipolar]
			)
		],
		fx: [
			(
				name: "Reverb",
				uname: \reverb,
				args: ["Mix", "Room", "Damp"],
				specs: [\unipolar, \unipolar, \unipolar]
			),
			(
				name: "Flanger",
				uname: \flanger,
				args: ["Mix", "Rate", "Feedback", "Depth"],
				specs: [\unipolar, \widefreq, \unipolar, specs[\smalldelay]]
			),
			(
				name: "Chorus",
				uname: \chorus,
				args: ["Mix", "Rate", "Offset", "Depth"],
				specs: [\unipolar, \widefreq, specs[\smalldelay], specs[\smalldelay]]
			),
			(
				name: "Phaser",
				uname: \phaser,
				args: ["Mix", "Rate", "Feedback", "Depth"],
				specs: [\unipolar, \widefreq, \unipolar, specs[\smalldelay]]
			),
			(
				name: "Delay",
				uname: \delay,
				args: ["Mix", "Damp", "Delay left", "Delay right"],
				specs: [\unipolar, \freq, specs[\smalldelay], specs[\smalldelay]]
			),
			(
				name: "Comb",
				uname: \comb,
				args: ["Mix", "Delay", "Offset", "Decay"],
				specs: [\unipolar, \delay, \delay, \decay]
			),
		],
		modulator: [
			(
				name: "LFO",
				uname: \lfo,
			),
			(
				name: "Performer",
				uname: \performer,
			),
			(
				name: "Stepper",
				uname: \stepper,
			)
		],
		spectrum: [
			(
				name: "Normal",
				uname: \normal,
			),
			(
				name: "Bend",
				uname: \bend,
			),
			(
				name: "Formant",
				uname: \formant,
			),
			(
				name: "Clip",
				uname: \clip,
			),
			(
				name: "Wrap",
				uname: \wrap,
			),
			(
				name: "Fold",
				uname: \fold,
			)
		],
		routing_insert: [
			(
				name: "Before filter 1",
				uname: \before_filter1,
			),
			(
				name: "Before filter 2",
				uname: \before_filter2,
			),
			(
				name: "After filter 1",
				uname: \after_filter1,
			),
			(
				name: "After filter 2",
				uname: \after_filter2,
			),
			(
				name: "Between filters",
				uname: \between_filters,
			),
			(
				name: "Before pan",
				uname: \before_pan,
			),
			(
				name: "In feedback",
				uname: \in_feedback,
			)
		],
		routing_feedback: [
			(
				name: "After filter 1",
				uname: \after_filter1,
			),
			(
				name: "After filter 2",
				uname: \after_filter2,
			),
			(
				name: "Between filters",
				uname: \between_filters,
			),
			(
				name: "Before pan",
				uname: \before_pan,
			),
			(
				name: "After pan",
				uname: \after_pan,
			)
		],
		routing_bypass_osc: [
			(
				name: "No bypass",
				uname: \off,
			),
			(
				name: "Osc 1",
				uname: \osc1,
			),
			(
				name: "Osc 2",
				uname: \osc2,
			),
			(
				name: "Osc 3",
				uname: \osc3,
			)
		],
		routing_bypass_dest: [
			// TODO
			(
				name: "Before Fx 1",
				uname: \before_fx1,
			),
			(
				name: "Before Fx 2",
				uname: \before_fx2,
			),
			(
				name: "Before Eq",
				uname: \before_eq,
			)
		],
	);

	module_kinds;

};

~make_param_data = {
	var params = ();
	var specs = ~passive_specs;

	///////// osc params
	3.do { arg idx;
		var osc;
		idx = idx+1;
		osc = ("osc"++idx).asSymbol;
		params[osc] = [
			(
				uname: (osc++"_pitch").asSymbol,
				name: "Pitch",
				kind: \knob,
				spec: specs[\pitch],
				numslot: 2
			),
			(
				uname: (osc++"_wt_pos").asSymbol,
				name: "Wt-pos",
				kind: \knob,
				spec: specs[\wt_pos],
				numslot: 3
			),
			(
				uname: (osc++"_intensity").asSymbol,
				name: "Intensity",
				kind: \knob,
				spec: \unipolar.asSpec,
				numslot: 3
			),
			(
				uname: (osc++"_amp").asSymbol,
				name: "Amp",
				kind: \knob,
				spec: specs[\amp],
				val: 0.5,
				numslot: 2
			),
			(
				uname: osc,
				name: "Osc"++idx,
				kind: \mute
			),
			(
				uname: (osc++"_wt").asSymbol,
				name: "Wt",
				indexes: idx,
				kind: \wavetable
			),
			(
				uname: (osc++"_fader").asSymbol,
				name: "Filter output",
				numslot: 0,
				spec: \unipolar.asSpec,
				kind: \knob
			),
			(
				uname: (osc++"_spectrum").asSymbol,
				name: "spectrum",
				bank: \spectrum,
				kind: \kind
			),
		];
	};

	///////// modosc params
	params[\modosc] = [
		(
			uname: \modosc_pitch,
			name: "Pitch",
			kind: \knob,
			spec: specs[\pitch],
			numslot: 2
		),
		(
			uname: \modosc_ring,
			name: "Amp",
			kind: \knob,
			spec: specs[\amp],
			numslot: 2
		),
		(
			uname: \modosc_phase,
			name: "Phase",
			kind: \knob,
			spec: \unipolar.asSpec,
			numslot: 2
		),
		(
			uname: \modosc_position,
			name: "Position",
			kind: \knob,
			spec: \unipolar.asSpec,
			numslot: 2
		),
		(
			uname: \modosc_filterfm,
			name: "Filter FM",
			kind: \knob,
			spec: \unipolar.asSpec,
			numslot: 2
		),
		(
			uname: \modosc_matrix,
			name: "Mod routing matrix",
			kind: \matrix
		),
		(
			uname: \modosc,
			name: "Modulation Osc",
			kind: \mute
		),
	];

	///////// noise params
	params[\noise] = [
		(
			uname: \noise_color,
			name: "N/A",
			kind: \knob,
			spec: \unipolar.asSpec,
			numslot: 2
		),
		(
			uname: \noise_amp,
			name: "Amp",
			kind: \knob,
			spec: specs[\amp],
			numslot: 2
		),
		(
			uname: \noise,
			name: "Noise",
			kind: \mute
		),
		(
			uname: \noise_kind,
			name: "Noise",
			bank: \noise,
			kind: \kind
		),
		(
			uname: \noise_fader,
			name: "Noise fader",
			kind: \knob,
			spec: \unipolar.asSpec,
			numslot: 0
		)
	];

	///////// feedback params
	params[\feedback] = [
		(
			uname: \feedback_amp,
			name: "Amp",
			kind: \knob,
			spec: ControlSpec(0, 6, 'amp', 0, 0.1, ""),
			numslot: 2
		),
		(
			uname: \feedback,
			name: "Feedback",
			kind: \mute
		),
		(
			uname: \feedback_fader,
			name: "Feedback fader",
			kind: \knob,
			spec: \unipolar.asSpec,
			numslot: 0
		)
	];

	//////// filter params
	2.do { arg idx;
		var osc;
		idx = idx+1;
		osc = ("filter"++idx).asSymbol;
		params[osc] = [
			(
				uname: (osc++"_arg1").asSymbol,
				name: "Arg1",
				arg_index: 0,
				kind: \multiknob,
				spec: \freq.asSpec,
				numslot: 3
			),
			(
				uname: (osc++"_arg2").asSymbol,
				name: "Arg2",
				arg_index: 1,
				kind: \multiknob,
				spec: \rq.asSpec,
				numslot: 3
			),
			(
				uname: (osc++"_arg3").asSymbol,
				name: "Arg3",
				arg_index: 2,
				kind: \multiknob,
				spec: \rq.asSpec,
				numslot: 2
			),
			(
				uname: osc,
				name: "Filter "++idx,
				kind: \mute
			),
			(
				uname: (osc++"_kind").asSymbol,
				name: "filter kind",
				bank: \filter,
				knobs: 3.collect { arg i; (osc++"_arg"++(i+1)).asSymbol },
				kind: \kind
			),
			(
				uname: (osc++"_amp").asSymbol,
				name: "Filter Amp",
				kind: \knob,
				spec: specs[\amp],
				numslot: 0
			)
		];
	};

	//////// others params
	params[\others] = [
		(
			uname: \filter_parseq,
			name: "ParSeq",
			kind: \knob,
			spec: \unipolar.asSpec,
			numslot: 2
		),
		(
			uname: \filter_mix,
			name: "F Mix",
			kind: \knob,
			spec: \unipolar.asSpec,
			numslot: 2
		)
	];


	//////// master pan params
	params[\master_pan] = [
		(
			uname: \amp_mod,
			//name: "Amp Mod",
			name: "N/A",
			kind: \knob,
			spec: specs[\amp],
			numslot: 2
		),
		(
			uname: \pan,
			name: "Pan",
			kind: \knob,
			spec: \pan.asSpec,
			numslot: 2
		),
		(
			uname: \master_pan,
			name: "Amp",
			kind: \mute
		)
	];

	//////// bypass params
	params[\bypass] = [
		(
			uname: \bypass_mod,
			//name: "Byp Mod",
			name: "N/A",
			kind: \mod_slot,
			numslot: 2
		),
		(
			uname: \bypass,
			name: "Bypass",
			kind: \mute
		),
		(
			uname: \bypass_amp,
			name: "Bypass Amp",
			spec: specs[\amp],
			kind: \knob,
			numslot: 0
		)
	];

	//////// master params
	params[\master] = [
		(
			uname: \amp,
			name: "Master Vol",
			kind: \knob,
			val: 1,
			spec: specs[\wideamp],
			numslot: 0
		),
		(
			// fake muter: can't mute master
			uname: \master,
			name: "",
			kind: \mute
		),
	];

	//////// FX params
	2.do { arg idx;
		var osc;
		idx = idx+1;
		osc = ("fx"++idx).asSymbol;
		params[osc] = [
			(
				uname: (osc++"_arg1").asSymbol,
				name: "Arg1",
				arg_index: 0,
				kind: \multiknob,
				spec: \unipolar.asSpec,
				numslot: 2
			),
			(
				uname: (osc++"_arg2").asSymbol,
				name: "Arg2",
				arg_index: 1,
				kind: \multiknob,
				numslot: 2
			),
			(
				uname: (osc++"_arg3").asSymbol,
				name: "Arg3",
				arg_index: 2,
				kind: \multiknob,
				numslot: 2
			),
			(
				uname: (osc++"_arg4").asSymbol,
				name: "Arg4",
				arg_index: 3,
				kind: \multiknob,
				numslot: 2
			),
			(
				uname: osc,
				name: "FX"++idx,
				kind: \mute
			),
			(
				uname: (osc++"_kind").asSymbol,
				name: "fx kind",
				bank: \fx,
				knobs: 4.collect { arg idx; (osc++"_arg"++(idx+1)).asSymbol },
				kind: \kind
			)
		];
	};

	
	//////// Eq params
	params[\eq] = [
			(
				uname: \eq_lowshelf,
				name: "Low Shelf",
				arg_index: 0,
				kind: \multiknob,
				spec: specs[\boost],
				numslot: 2
			),
			(
				uname: \eq_boost,
				name: "Boost",
				arg_index: 1,
				kind: \multiknob,
				spec: specs[\boost],
				numslot: 2
			),
			(
				uname: \eq_freq,
				name: "Frequency",
				arg_index: 2,
				kind: \multiknob,
				spec: \freq.asSpec,
				numslot: 2
			),
			(
				uname: \eq_highshelf,
				name: "High Shelf",
				arg_index: 3,
				kind: \multiknob,
				spec: specs[\boost],
				numslot: 2
			),
			(
				uname: \eq,
				name: "Eq",
				kind: \mute
			)

	];

	//////// Insert effects params
	2.do { arg idx;
		var osc;
		idx = idx+1;
		osc = ("insert"++idx).asSymbol;
		params[osc] = [
			(
				uname: (osc++"_arg1").asSymbol,
				name: "Arg1",
				kind: \multiknob,
				arg_index: 0,
				spec: \unipolar.asSpec,
				numslot: 2
			),
			(
				uname: (osc++"_arg2").asSymbol,
				name: "Arg2",
				kind: \multiknob,
				arg_index: 1,
				numslot: 2
			),
			(
				uname: osc,
				name: "Insert "++idx,
				kind: \mute
			),
			(
				uname: (osc++"_kind").asSymbol,
				name: "insert kind",
				bank: \insert,
				knobs: 2.collect { arg idx; (osc++"_arg"++(idx+1)).asSymbol },
				kind: \kind
			)
		];
	};

	//////// Env params

	4.do { arg idx;
		var osc;
		idx = idx+1;
		osc = ("env"++idx).asSymbol;

		params[osc] = [
			(
				uname: (osc++"_vel").asSymbol,
				name: "Vel",
				kind: \knob,
				val: 0,
				spec: specs[\velocity],
				numslot: 0
			),
			(
				uname: (osc++"_ktr").asSymbol,
				name: "KTR",
				kind: \knob,
				val:0,
				spec: specs[\ktr],
				numslot: 0
			),
			(
				uname: (osc++"_delay_time").asSymbol,
				name: "Delay",
				kind: \knob,
				spec: \delay.asSpec,
				val: 0,
				numslot: 1
			),
			(
				uname: (osc++"_attack_time").asSymbol,
				name: "Attack Time",
				kind: \knob,
				spec: specs[\env],
				numslot: 1
			),
			(
				uname: (osc++"_attack_level").asSymbol,
				name: "Attack level",
				kind: \knob,
				spec: specs[\envamp],
				numslot: 1
			),
			(
				uname: (osc++"_decay_time").asSymbol,
				name: "Decay time",
				kind: \knob,
				spec: specs[\env],
				numslot: 1
			),
			(
				uname: (osc++"_decay_level").asSymbol,
				name: "Decay level",
				kind: \knob,
				spec: specs[\envamp],
				val: 0.8,
				numslot: 1
			),
			(
				uname: (osc++"_sustain_time").asSymbol,
				name: "Sustain loop",
				kind: \knob,
				spec: specs[\env],
				numslot: 1
			),
			(
				uname: (osc++"_sustain_level").asSymbol,
				name: "Sustain level",
				kind: \knob,
				spec: specs[\envamp],
				numslot: 1
			),
			(
				uname: (osc++"_release_time").asSymbol,
				name: "Release",
				kind: \knob,
				spec: specs[\env],
				numslot: 1
			)
		]
	};

	//////// Modulator params (lfo, stepper, performer)

	4.do { arg idx;
		var osc;
		idx = idx+1;
		osc = ("modulator"++idx).asSymbol;

		params[osc] = [
			(
				uname: (osc++"_kind").asSymbol,
				name: "kind",
				kind: \kind,
				bank: \modulator
			),
			(
				uname: (osc++"_rate").asSymbol,
				name: "Rate",
				kind: \knob,
				val: 1,
				spec: specs[\rate],
				numslot: 1
			),
			(
				uname: (osc++"_amp").asSymbol,
				name: "Amp",
				kind: \knob,
				spec: specs[\amp],
				numslot: 1
			),
			(
				uname: (osc++"_ampmod").asSymbol,
				name: "Amp Mod",
				kind: \knob,
				spec: specs[\amp],
				numslot: 1
			),
			(
				uname: (osc++"_glidefade").asSymbol,
				name: "Glide/Fade",
				kind: \knob,
				spec: specs[\glidefade],
				numslot: 1
			),
			(
				uname: (osc++"_steps1").asSymbol,
				name: "Steps1",
				range: [0,0.5],
				indexes: [idx-1, \ampmod],
				kind: \steps
			),
			(
				uname: (osc++"_steps2").asSymbol,
				name: "Steps2",
				indexes: [idx-1, \glidefade],
				kind: \steps
			),
			(
				uname: (osc++"_steps_amp").asSymbol,
				name: "Steps amp",
				indexes: [idx-1, \amp],
				kind: \steps
			),
			(
				uname: (osc++"_curve1").asSymbol,
				name: "Curve1",
				indexes: [idx-1, 0],
				kind: \curve
			),
			(
				uname: (osc++"_curve2").asSymbol,
				name: "Curve2",
				indexes: [idx-1, 1],
				kind: \curve
			),
			(
				uname: (osc++"_perfcurve1").asSymbol,
				name: "Curve1",
				indexes: [idx-1, 0],
				kind: \perfcurve
			),
			(
				uname: (osc++"_perfcurve2").asSymbol,
				name: "Curve2",
				indexes: [idx-1, 1],
				kind: \perfcurve
			),
			(
				uname: (osc++"_env_attack").asSymbol,
				name: "Attack",
				kind: \knob,
				spec: specs[\env],
				numslot: 0
			),
			(
				uname: (osc++"_env_decay").asSymbol,
				name: "Decay",
				kind: \knob,
				spec: specs[\env],
				numslot: 0
			),
			//(
			//	uname: (osc++"_phase1").asSymbol,
			//	name: "phase",
			//	kind: \knob,
			//	spec: \unipolar.asSpec,
			//	numslot: 0
			//),
			//(
			//	uname: (osc++"_phase2").asSymbol,
			//	name: "phase",
			//	kind: \knob,
			//	spec: \unipolar.asSpec,
			//	numslot: 0
			//),
		];
	};

	//////// Macro params

	params[\macro] = 8.collect { arg idx;
		var osc;
		idx = idx+1;
		osc = ("macro"++idx).asSymbol;

		(
			uname: (osc++"_control").asSymbol,
			name: "Macro "++idx,
			arg_index: idx-1,
			kind: \macro,
			val: 0,
			slotnum: 0,
		)
	};

	/////////// vibrato

	params[\vibrato] = [
			(
				uname: \vibrato,
				name: "Vibrato",
				val: 1,
				kind: \mute,
			),
			(
				uname: \vibrato_rate,
				name: "Rate",
				kind: \knob,
				numslot: 1,
				spec: ControlSpec(0.001, 20, \exp, 0, 0.1, units: " Hz"),
				transmit: \bus
			),
			(
				uname: \vibrato_depth,
				name: "Depth",
				kind: \knob,
				numslot: 1,
				val: 0,
				spec: ControlSpec(0.00001, 1, \exp, 0, 0, units: " Hz"),
				transmit: \bus
			),
			(
				uname: \vibrato_env_attack,
				name: "Attack",
				kind: \knob,
				spec: specs[\env],
				numslot: 0
			),
			(
				uname: \vibrato_env_decay,
				name: "Decay",
				kind: \knob,
				spec: specs[\env],
				numslot: 0
			),
	];

	//////// Routing

	params[\routing] = {

		var names = [\insert1, \insert2, \feedback, \bypass_osc, \bypass_dest];
		var labels = ["Ins1", "Ins2", "Feedback", "Bypass source", "Bypass destination"];
		names.collect { arg name, idx;
			(
				uname: ("routing_"++name).asSymbol,
				name: labels[idx],
				bank: ("routing_"++name.asString.replace("1", "").replace("2", "")).asSymbol,
				routing_name: name,
				kind: \kind,
				transmit: \routing
			)
		}

	}.value;

	//////// Voicing

	//voicing unisono
	//voicing pitch_lorange
	//voicing pitch_hirange
	//voicing pitch_chord
	//voicing wavetable_lorange
	//voicing wavetable_hirange

	//voicing enable_pan_spreading
	//enabled enable_pitch_spreading
	//enabled wavetable_spreading

	//pitch_spread
	//wavetable_spread
	//pan_spread

	params[\voicing] = [
			(
				uname: \voicing_unisono,
				kind: \static_knob,
				numslot: 0,
				spec: ControlSpec.new(1,64,\lin,1,1),
				transmit: \voicing
			),
			(
				uname: \voicing_pitch_lorange,
				kind: \spec_knob,
				destination: \pitch_spread,
				spec_bound: \minval,
				numslot: 0,
				val: -1,
				spec: specs[\pitch],
				transmit: \voicing
			),
			(
				uname: \voicing_pitch_hirange,
				kind: \spec_knob,
				destination: \pitch_spread,
				spec_bound: \maxval,
				numslot: 0,
				val: 1,
				spec: specs[\pitch],
				transmit: \voicing
			),
			(
				uname: \voicing_wavetable_lorange,
				kind: \spec_knob,
				destination: \wavetable_spread,
				spec_bound: \minval,
				numslot: 0,
				val: -1,
				spec: \bipolar.asSpec,
				transmit: \voicing
			),
			(
				uname: \voicing_wavetable_hirange,
				kind: \spec_knob,
				destination: \wavetable_spread,
				spec_bound: \maxval,
				numslot: 0,
				val: 1,
				spec: \bipolar.asSpec,
				transmit: \voicing
			),
			(
				uname: \voicing_pan_lorange,
				kind: \spec_knob,
				destination: \pan_spread,
				spec_bound: \minval,
				numslot: 0,
				val: -1,
				spec: \bipolar.asSpec,
				transmit: \voicing
			),
			(
				uname: \voicing_pan_hirange,
				kind: \spec_knob,
				destination: \pan_spread,
				spec_bound: \maxval,
				numslot: 0,
				val: 1,
				spec: \bipolar.asSpec,
				transmit: \voicing
			),
			(
				uname: \voicing_enable_pitch,
				kind: \mute,
				val: 1,
				transmit: \voicing
			),
			(
				uname: \voicing_enable_wavetable,
				kind: \mute,
				val: 1,
				transmit: \voicing
			),
			(
				uname: \voicing_enable_pan,
				kind: \mute,
				val: 1,
				transmit: \voicing
			),

			(
				uname: \pitch_spread,
				name: "Pitch cutoff",
				kind: \knob,
				val: 0,
				spec: specs[\pitch],
				numslot: 1,
			),
			(
				uname: \wavetable_spread,
				name: "Wavetable position",
				kind: \knob,
				val: 0,
				spec: \bipolar.asSpec,
				numslot: 1,
			),
			(
				uname: \pan_spread,
				name: "Pan",
				kind: \knob,
				val: 0,
				spec: \bipolar.asSpec,
				numslot: 1,
			),
	];

	////////// ktr

	params[\ktrcurve] = [
			(
				uname: \ktrcurve_osc,
				name: "Ktr Osc",
				kind: \ktrcurve,
				curves: (
					linear: [[0,1/4,2/4,3/4,1],[0,1/4,2/4,3/4,1]],
					off: [[0,1/4,2/4,3/4,1],[0,0,0,0,0]+0.5],
					user: [[0,1/4,2/4,3/4,1],[0,1/4,2/4,3/4,1]]
				),
				columns: [
					[\target, "Target"],
					[\linear, "Linear"],
					[\off, "Off"],
					[\user, "User"]
				],
				rows: [
					[\osc1, "Osc 1"],
					[\osc2, "Osc 2"],
					[\osc3, "Osc 3"],
					[\mosc, "M Osc"],
					[\insfx, "InsFx"],
				],
				val: (
					\osc1: \linear,
					\osc2: \linear,
					\osc3: \linear,
					\mosc: \linear,
					\insfx: \linear,
				),
				editable: [\user],
				destinations: (
					\osc1: \osc1_pitch,
					\osc2: \osc2_pitch,
					\osc3: \osc3_pitch,
					\mosc: \modosc_pitch,
					//\insfx: \blabla,

				)
			),
			(
				uname: \ktrcurve_filter,
				name: "Ktr Filter",
				kind: \ktrcurve,
				curves: (
					linear: [[0,1/4,2/4,3/4,1],[0,1/4,2/4,3/4,1]],
					off: [[0,1/4,2/4,3/4,1],[0,0,0,0,0]+0.5],
					user1: [[0,1/4,2/4,3/4,1],[0,1/4,2/4,3/4,1]],
					user2: [[0,1/4,2/4,3/4,1],[0,1/4,2/4,3/4,1]]
				),
				columns: [
					[\target, "Target"],
					[\linear, "Linear"],
					[\off, "Off"],
					[\user1, "User 1"],
					[\user2, "User 2"]
				],
				rows: [
					[\filter1, "Filter 1"],
					[\filter2, "Filter 2"],
				],
				val: (
					\filter1: \linear,
					\filter2: \linear,
				),
				editable: [\user1, \user2],
				//destinations: (
				//	\osc1: \osc1_pitch,
				//	\osc2: \osc2_pitch,
				//	\osc3: \osc3_pitch,
				//	\mosc: \modosc_pitch,
				//	//\insfx: \blabla,

				//)
			)
	];

	////////// presets

	params[\presets] = [
			(
				uname: \presets_global,
				name: "Global presets",
				kind: \preset,
			)
	];


	params;

};
