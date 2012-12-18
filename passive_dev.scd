GUI.qt
Quarks.gui

(
	s.waitForBoot {	"~/code/sc/passive/main.scd".standardizePath.load; }
)
Debug.enableDebug = true
Debug.enableDebug = false
(
~load_passive.();
~new_passive.();
~passive.load_preset_by_uname("default");
~passive.make_gui;
//~passive.build_synthdef;
//~midiresp = ~passive.make_midi_responder;
"********************************************************************done".debug;
)
~passive.load_preset_by_uname("default");

(
~load_passive.();
~new_passive.();
~passive.load_preset_by_uname("default");
//~passive.load_preset_by_uname("alien indus");
~passive.make_gui;
//~passive.build_synthdef;
~midiresp = ~passive.make_midi_responder

)
(
~load_passive.();
~passive.build_synthdef;

)


(
~play_notes = { arg passive;
	var piano;
	var note;
	piano = passive.get_piano;
	Task {
		loop {
			note = piano.(300);
			2.wait;
			note.release_node;
			1.wait;
		}
	}.play
};
)
(

~a = ~class_passive_controller.new("bla");
~a.load_preset_by_uname("alien indus");
~play_notes.(~a);
~a.synthdef_name

~b = ~class_passive_controller.new("rho");
~b.load_preset_by_uname("lancinant");
~play_notes.(~b);
)
UniqueID.next


~fx_group
Bus.
~out_bus = Bus.new(\audio, 0, 1)
~bla = Bus.audio(s)
~bla

~passive.fx_bus = ~out_bus
~passive.build_synthdef
~passive.synthdef_args[\out]
~passive.synthdef_name
~passive.synthdef_fx_name

~modbus = 8.collect { arg x; Bus.audio(s, 1) }
~note_group.nodeID
(
~note_group = Group.new(s);
~fx_group = Group.after(~note_group);
//Synth(\passive_fx_default, [\in, ~passive.fx_bus.index], target:~fx_group);

~pat = 
Pbind(
	\degree, Pseq([0, 2, 4],inf),
	\dur, 2,
	\freq, Pfunc { arg ev;
		Scale.major.degreeToFreq(ev[\degree], 60.midicps, 0);
	},
	//\freq, Pseq([200,300],4),

	\doneAction, 14,
	\legato, 0.7,
	//\fx_bus, 0,
	//\fx_bypass_bus, 7,
	//\group, ~note_group,
	\instrument, Pfunc { ~passive.synthdef_name.debug("instr").dump },
	//\instrument, \piano2,
	\velocity, 0.1,
	\modulation_bus, Pfunc{ arg ev;
		var group = Group.new(~note_group);
		var modbus = 8.collect { arg x; Bus.audio(s, 1) };
		var watcher;
		var endfunc;
		ev[\group] = group.nodeID;
		watcher = NodeWatcher.register(group);

		endfunc = { arg obj, what;
			what.debug("what ?");
			if(what == \n_end) {
				"fin du group".debug;
				group.removeDependant(endfunc);
				modbus.do { arg x; x.free };
			}
		};
		group.addDependant(endfunc);
		[modbus.collect{ arg x; x.index }]
	},
	\compute_freqi, Pfunc { arg ev;
		var dict = ~passive.compute_freq(ev[\freq]);
		ev[\freq].debug("freq");
		dict.debug("dict");
		ev.putAll(dict);
		1;
	}
) <> Pbind( * ~passive.synthdef_args.getPairs );

~patfx = PmonoArtic(*(
	[
		~passive.synthdef_fx_name, 
		\group, ~fx_group,
		\legato, Pfunc { 
			if(~passive.rebuild_synthdef) {
				~passive.rebuild_synthdef = false;
				0.999;
			} {
				1
			} 
		}
	] ++ ~passive.synthdef_args.getPairs
));

~fpat = Ppar([
	~pat,
	~patfx,
]);
~fpat.trace.play
)

~passive.rebuild_synthdef

4.degreeToKey(Scale.ionian)
Scale.ionian
s.plotTree

(
)

~passive.synthdef_args[\modulation_bus]



~passive.fx_bus

{ Out.ar(0, In.ar(~passive.fx_bus.index)) }.play(target:~fx_group)

(
SynthDef(\nullfx, { arg in, out=0;
	in = In.ar(in, 2);
	Out.ar(out, in);
}).add;
)








{SinOsc.ar(120)}.play
s.quit
s.boot
~midiresp = ~passive.make_midi_responder

Synth(\passive, [\gate, 1])

(
~load_passive.();
~passive.build_synthdef;
~piano = ~passive.get_piano;
Task {
	
	loop {

		~sy = ~piano.(300);
		2.wait;
		~sy.release_node;
		1.wait;
	}
}.play
)

(

~load_passive.();
~new_passive.();
"fou".debug;
//~class_load_wavetable_dialog.new;
)

~preset = ~passive.save_preset
~passive.load_preset(~preset)

~passive.modulation_manager.get_instr_modulation
~passive.modulation_manager.get_disabled_modulators
~passive.modulation_manager.source_dict
~passive.get_arg(\modulator1_curve1).save_data

~modman = ~preset[\modulation_manager]

~passive.get_arg(\ktrcurve_osc).get_transfert_function(\osc1).(10,128)
~passive.get_arg(\macro1_control).model
~passive.get_arg(\modulator1_rate).model
~passive.get_arg(\vibrato_depth).model
~passive.get_arg(\modulator1_kind).model
~passive.modulation_manager.get_polarity([\mod, 3])

~passive.modulation_manager.get_source(\osc1_pitch, 0)
~passive.modulation_manager.get_instr_modulation
~passive.modulation_manager.get_instr_modulation[\osc1_pitch]
~passive.modulation_manager.slot_dict[[\osc1_pitch, 0.0]]
~passive.modulation_manager.modulation_dict[[\osc1_pitch, 1]]
n = Dictionary.new;
n[[\osc1_pitch, 0]] = 1
~passive.modulation_manager.slot_dict.keysValuesDo { arg key, val; n[key] = val }
~passive.modulation_manager.slot_dict.keysValuesDo { arg key, val; n[key] = val }
~passive.modulation_manager.slot_dict.keys.do {
	~passive.modulation_manager.slot_dict[key] = val;

}

~a = 1
~a
~a.ref
~r = Ref(~a)
~r.value = 4
~agt
~passive.get_arg(\voicing_unisono)
~passive.get_arg(\voicing_wavetable_lorange).model.val
~passive.get_arg(\voicing_wavetable_lorange).model.spec
\unipolar.asSpec
~passive.get_arg(\pitch_spread).get_bus.get{arg bus; bus.debug("bus")}
~passive.get_arg(\pitch_spread).get_bus.get{arg bus; bus.debug("bus")}

~passive.synthdef_args.keysValuesDo { arg key, val; val.debug(key)}
~passive.synthdef_args.getPairs
~passive.synthdef_ns_args[0] // enabled
~passive.synthdef_ns_args[1] // kind
~passive.synthdef_ns_args[2] // mod
~passive.synthdef_ns_args[3][\voicing]
~passive.synthdef_ns_args[3] // routing
~passive.synthdef_ns_args[4]
[ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1  ][1..9]

~passive.synthdef_ns_args[2][\osc1_pitch][1]

~pass
~passive.synthdef_ns_args.postcs
~passive.build_synthdef;
~passive.synthdef_args[\osc1_wt_pos].get{ arg bus; bus.debug("bus") }
~passive.synthdef_args[\freq]
~buf = ~passive.synthdef_args[\osc1_wt]
{ VOsc.ar(~buf, MouseX.kr(20, 380),mul:0.1) ! 2  }.play;
~passive.synthdef_args[\enabled]
~passive.synthdef_args[\routing]
~passive.synthdef_args[\modulator1_curve1]
~passive.synthdef_args[\steps][0]
~passive.synthdef_args[\kinds]
~passive.synthdef_args[\mod]
~passive.synthdef_args[\mod][\osc1_pitch][0][\source]
~passive.modulation_manager.get_instr_modulation
~passive.synthdef_args[\filter1_amp].get{ arg bus; bus.debug("bus") }
~passive.synthdef_args[\pitch_spread].get{ arg bus; bus.debug("bus") }
~passive.synthdef_args[\filter2_amp].get{ arg bus; bus.debug("bus") }
'c14'.asBus

~passive.get_arg(\filter).model.spec.map(0.6)

~passive.get_arg(\osc1_pitch).model.spec.map(0.6)
~passive.get_arg(\osc1_pitch).model.spec.range*1
~passive.get_arg(\osc1_wt).get_buffer
~passive.get_arg(\osc1_pitch).get_bus.get{ arg bus; bus.debug("bus") }

~passive.modulation_manager.source_dict


~passive.synthdef_args[\osc1_pitch]

BufferPool.annotations










~passive.make_gui;

~piano = ~passive.as_piano
~piano.()

~args = ~passive.build_synthdef
~args[\freq] = 200;

d = Patch(\passive, ~args);
d.play

~passive.synthdef
().synthdef = 41

~args
~poo = { "##############".postln };

~args = ~passive.synthdef_args.deepCopy

(
~args = ();
[\enabled, \mod, \kinds, \routing, \steps].do { arg key;
	~args[key] = ~passive.synthdef_args[key]
};
)
(
~args = ();
[\enabled, \mod, \kinds, \routing, \steps].do { arg key;
	~args[key] = ~passive.synthdef_args[key]
};
)

~args[\enabled] = 0
~args[\mod] = 0
~args[\routing] = 0
~args[\kinds] = 0
~args[\steps] = 0
~args[\msteps] = 0
~args[\steps].do(_.postln)
~args[\steps]
~args[\steps] = nil
~args[\msteps] = ~args[\steps][0]]
~args.keys
~args
~args[\gate] = nil

~args[\msteps] = [ ( 'glidefade': [ 1, 0.5, 1, 0.5, 1, 0.5, 1, 0.5, 1, 0.5, 1, 0.5, 1, 0.5, 1, 0.5  ]  ) ]
~args[\msteps] = 0

~poo.(); ~sd = Instr(\passive).asSynthDef( ~passive.synthdef_args )
~poo.(); ~sd = Instr(\passive).asSynthDef( ~args )
~sd.add;
Synth(~sd.name, [\freq, 200]);

~poo.(); ~sd = 
Patch(\passive,  ~passive.synthdef_args ).prepareForPlay
~passive.synthdef_args[\routing][\modosc]
~passive.synthdef_args[\enabled]
~sd.release
~poo.(); ~sd = Patch(\passive,  ~args ).prepareForPlay.spawn(atTime:0.1)
~poo.(); ~sd = Patch(\passive,  ~args ).play


~args[\osc1_wt]
~args[\osc1_amp]
~args[\osc1_fader]
~args[\osc1_pitch]
Bus.getSynchronous(36)
Bus.new(\control, 34, 1).get({ arg val; val.debug("bus") })
~passive.bus_dict[\osc1_fader].get({ arg val; val.debug("bus") })
~passive.bus_dict[\osc1_amp].get({ arg val; val.debug("bus") })
~passive.bus_dict[\osc1_pitch].get({ arg val; val.debug("bus") })
~passive.bus_dict[\osc1_fader].set(40)
~passive.get_arg(\osc1_amp).get_norm_val
b = Bus.control(s, 1)
b.set(10)
b.get({ arg val; val.debug("bus") })

(
~load_passive.();

~passive = ~class_passive_controller.new;
//~passive.make_gui;
~passive.build_synthdef.keys.asArray.sort.do (_.postln);
//~passive.modulation_manager.bind([\mod, 1], [\osc1_amp, 0]);
//~passive.modulation_manager.get_instr_modulation.postcs;
"********************************************************************done".debug;
)

~passive.build_synthdef; 1
~passive.get_mod_arg(0,\curve1).model
~passive.get_env_arg(1, \decay_time)
(

~load_passive.();

w = Window.new;
		e = EnvelopeView(w, (150@100).asRect)
		    .drawLines_(true)
		    .selectionColor_(Color.red)
		    .drawRects_(true)
		//    .resize_(5)
		    .step_(0.05)
		    .action_({arg b; [b.index, b.value].postln})
		    .thumbSize_(5);
e.setEnv( Env.perc(0.5,0.5, 1, 0) );
w.front;

)

(

"~/supercollider/sc/passive/ui.sc".standardizePath.loadPath;
"~/supercollider/sc/passive/control.sc".standardizePath.loadPath;

w = Window.new;
~bla = ~class_lfoperfstep_frame.new(w,800@350, \lfo, ~curvebank);
w.front;

)

(

"~/supercollider/sc/passive/ui.sc".standardizePath.loadPath;
"~/supercollider/sc/passive/control.sc".standardizePath.loadPath;

w = Window.new;
~bla = ~class_master_view.new(w);
w.front;

)
(

"~/supercollider/sc/passive/ui.sc".standardizePath.loadPath;
"~/supercollider/sc/passive/control.sc".standardizePath.loadPath;

w = Window.new;
~bla = ~class_center_frame_view.new(w);
w.front;

)
MIDIClient.restart
MIDIClient.init
MIDIClient.initialized
