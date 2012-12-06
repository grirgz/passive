GUI.qt
Quarks.gui

(
	"~/code/sc/passive/main.sc".standardizePath.load;
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
