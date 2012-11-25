~load_curve_in_buffer = { arg buffer, curvefunc;
	var size = buffer.numFrames;
	size.debug("load_curve_in_buffer: size");
	buffer.debug("buffer");
	buffer.loadCollection(FloatArray.fill(size, { arg i;
		curvefunc.(i/size)
	}),0, { "done".debug; })
};

~load_curve_in_wavetable_buffer = { arg buffer, curvefunc;
	var size = buffer.numFrames;
	var wt, sig;
	sig = Signal.newClear(size/2);
	sig.waveFill(curvefunc, 0, 1);

	size.debug("load_curve_in_wavetable_buffer: size");
	buffer.debug("buffer");
	buffer.loadCollection(sig.asWavetable,0, { "done".debug; });
};

~load_curvelist_in_buffer = { arg buffer, curvefunclist, curve_amps;
	var size = buffer.numFrames;
	var slicesize = (size/curvefunclist.size).asInteger;
	size.debug("load_curvelist_in_buffer: size");
	buffer.debug("buffer");
	curvefunclist.do { arg curvefunc, idx;
		buffer.loadCollection(FloatArray.fill(slicesize, { arg i;
			curvefunc.(i/slicesize) * curve_amps.wrapAt(idx)
		}), idx*slicesize, { "done".debug; })
	}
};

~load_sample_in_wavetable_buffer = { arg buffer, path;
	var file, sig;


	file = SoundFile.openRead(path);

	sig = Signal.newClear(file.numFrames);
	file.readData(sig);
	file.close; // close the file
	~load_signal_in_wavetable_buffer.(buffer, sig);

};

~load_signal_in_wavetable_buffer = { arg buffer, sig;
	var size, fsize;
	// resamp the table to have a pow of 2 (bigger to avoid aliassing)
	// if u read many diff samples choose a bigger pow of 2
	size = buffer.numFrames;
	fsize = sig.size;
	[fsize, size/2].debug("load_sample_in_wavetable_buffer: resampling");
	sig = sig.resamp1(size/2).as(Signal);

	// Convert it to a Wavetable
	sig = sig.asWavetable;

	buffer.loadCollection(sig);

};


~curvebank = (

//	saw4: { arg x; // bugged
//		var y;
//		y = sin(sqrt(x)/(x*2)).linlin(0,1,0,1);
//		y;
//	},
	saw1: { arg x;
		var y;
		y = x % 1;
		y;
	},
	square1: { arg x;
		var y;
		x = x % 1;
		y = if(x<0.5) { 0 } { 1 };
		y;
	},
	triangle1: { arg x;
		var y;
		x = x % 1;
		y = if(x<0.5) { 2*x } { 2*(1-x) };
		y;
	},
	sin1: { arg x;
		var y;
		y = sin(x*2pi).linlin(-1,1,0,1);
		y;
	},
	sin2: { arg x;
		var y;
		y = sin(x*pi);
		y;
	},
	sin4: { arg x;
		var y;
		y = sin(x*pi/2);
		y;
	},
	line1: { arg x;
		var y;
		y = x;
		y;
	},
	negline1: { arg x;
		var y;
		y = 1-x;
		y;
	}

);
~curvebank.known = false;

////////////////////// super controllers

//~class_frame_controller = (
//	new: { arg self;
//		self = self.deepCopy;
//
//		self;
//	},
//
//	get_mute_controller: { arg self;
//		self.mute_controller;
//	},
//
//	get_popup_controller: { arg self;
//		self.popup_controller;
//	},
//
//	get_knob_controllers: { arg self;
//		self.knob_controllers;
//	},
//
//	get_args: { arg self;
//
//	}
//);
//
//~class_frame_filter_controller = (
//	parent: ~class_frame_controller,
//	new: { arg self, main_controller, idx;
//		var osc;
//		var kinds;
//		self = self.deepCopy;
//		
//		idx = idx+1;
//		osc = ("filter"++idx).asSymbol;
//
//		kinds = (
//			lpf: [
//				"Cutoff",
//			],
//			rlpf: [
//				"Cutoff",
//				"Resonance",
//			],
//			hpf: [
//				"Cutoff",
//			]
//		);
//
//		self.mute_controller = ~make_pparam_controller_from_kind.(main_controller, (
//				uname: osc,
//				name: "Filter 1",
//				kind: \mute
//		)); 
//
//		self.popup_controller = ~class_pparam_kind_controller.new(main_controller, self, (
//				uname: (osc++"_kind").asSymbol,
//				name: "filter kind",
//				bank: \filter,
//				kind: \kind
//		)); 
//
//		self.fader_controller = ~make_pparam_controller_from_kind.(main_controller, (
//				uname: (osc++"_amp").asSymbol,
//				name: "Filter Amp",
//				kind: \knob,
//				numslot: 0
//		)); 
//
//		self.knob_controllers = [
//			(
//				uname: (osc++"_arg1").asSymbol,
//				name: "Arg1",
//				kind: \knob,
//				numslot: 3
//			),
//			(
//				uname: (osc++"_arg2").asSymbol,
//				name: "Arg2",
//				kind: \knob,
//				numslot: 3
//			),
//			(
//				uname: (osc++"_arg3").asSymbol,
//				name: "Arg3",
//				kind: \knob,
//				numslot: 2
//			)
//		].collect { arg param;
//			~make_pparam_controller_from_kind.(main_controller, param);
//		};
//
//		self;
//	},
//
//	set_current_kind: { arg self, kind;
//		self.current_kind = kind;
//		self.knob_controllers = 
//
//	}
//
//);

////////////////////// controllers


~make_pparam_controller_from_kind = { arg main_controller, paramdata;
	switch(paramdata.kind,
		\knob, { ~class_pparam_controller.new(main_controller, paramdata) },
		\macro, { ~class_pparam_macro_controller.new(main_controller, paramdata) },
		\mod_slot, { ~class_pparam_controller.new(main_controller, paramdata) },
		\multiknob, { ~class_pparam_multi_controller.new(main_controller, paramdata) },
		\wavetable, { ~class_pparam_wavetable_controller.new(main_controller, paramdata) },
		\kind, { ~class_pparam_kind_controller.new(main_controller, paramdata) },
		\mute, { ~class_pparam_mute_controller.new(main_controller, paramdata) },
		\matrix, { ~class_pparam_modmatrix_controller.new(main_controller, paramdata) },
		\curve, { ~class_pparam_curve_controller.new(main_controller, paramdata) },
		\perfcurve, { ~class_pparam_perfcurve_controller.new(main_controller, paramdata) },
		\steps, { ~class_pparam_steps_controller.new(main_controller, paramdata) },
		\preset, { ~class_presets_global_controller.new(main_controller, paramdata) },
		{ ~class_pparam_controller.new(main_controller, paramdata) }
	);
};


~class_pparam_curve_controller = (
	model: (
		//FIXME: phase
		name: "Curve",
		kind: \curve,
		transmit: \curve,
		indexes: [0, 0],
		uname: \modulator1_curve1,
		curve: \sin1,
	),

	new: { arg self, controller, paramdata;
		self = self.deepCopy;
		self.main_controller = { arg self; controller };
		self.model.putAll(paramdata);
		self.buffer = Buffer.alloc(s, 512, 1);
		controller.register_buffer(self.buffer, self.model.uname);
		self.set_curve(self.model.curve);

		self;
	},

	save_data: { arg self;
		self.model;
	},

	load_data: { arg self, data;
		self.model.curve = data.curve;
		self.set_curve(self.model.curve)
	},

	get_menu_items_names: { arg self;
		self.get_curvebank.keys.asList.sort;
	},

	get_curvebank: { arg self;
		self.main_controller.get_curvebank;
	},

	set_curve: { arg self, val;
		var curvefun;
		curvefun = self.main_controller.get_curvebank[val];
		if(curvefun.notNil) {
			~load_curve_in_wavetable_buffer.(self.buffer, curvefun);
		} {
			val.debug("class_pparam_curve_controller: curve not valid");
		}
	},

	set_property: { arg self, name, val, update=true;
		[name, val, update].debug("class_pparam_controller.set_property");
		switch(name,
			\curve, { 
				self.set_curve(val)
			}
		);
		if(update) {
			self.changed(\set_property, name, val);
		}
	},

	refresh: { arg self;
		"REFRESH++".debug;
		self.changed(\set_property, \value, self.get_menu_items_names.indexOf(self.model.curve));
		self.changed(\set_property, \curve, self.model.curve);
	},

	get_buffer: { arg self;
		self.buffer;
	},

	get_val: { arg self;
		self.model.val;
	},
);

~class_pparam_perfcurve_controller = (
	model: (
		name: "Curve",
		kind: \curve,
		transmit: \curve,
		indexes: [0, 0],
		uname: \modulator1_perfcurve1,
		curve: \sin1 ! 16,
		curve_amps: 1 ! 16,
	),

	new: { arg self, controller, paramdata;
		self = self.deepCopy;
		self.main_controller = { arg self; controller };
		self.model.putAll(paramdata);
		self.buffer = Buffer.alloc(s, 2048, 1);
		controller.register_buffer(self.buffer, self.model.uname);
		self.set_curve(self.model.curve, self.model.curve_amps);

		self;
	},

	save_data: { arg self;
		self.model;
	},

	load_data: { arg self, data;
		self.model.curve = data.curve;
		self.model.curve_amps = data.curve_amps;
		self.set_curve(self.model.curve, self.model.curve_amps)
	},

	get_menu_items_names: { arg self;
		self.get_curvebank.keys.asList.sort;
	},

	get_curvebank: { arg self;
		self.main_controller.get_curvebank;
	},

	get_numstep: { arg self;
		//TODO
		16
	},

	set_curve: { arg self, val, curveamp; 
		var curvefunlist;
		self.model.curve = val;
		self.model.curve_amps = curveamp;
		curvefunlist = val.collect { arg curve;
			self.main_controller.get_curvebank[curve];
		};
		if(curvefunlist.notNil) {
			// FIXME: disabled for debug
			~load_curvelist_in_buffer.(self.buffer, curvefunlist, curveamp);
		} {
			val.debug("class_pparam_curve_controller: curve not valid");
		}
	},

	set_property: { arg self, name, val, update=true;
		[name, val, update].debug("class_pparam_controller.set_property");
		switch(name,
			\curve, { 
				self.set_curve(val, self.model.curve_amps);
			},
			\curve_amps, {
				self.set_curve(self.model.curve, val);
			}

		);
		if(update) {
			self.changed(\set_property, name, val);
		}
	},

	refresh: { arg self;
		"REFRESH++".debug;
		//self.changed(\set_property, \value, self.get_menu_items_names.indexOf(self.model.curve));
		self.changed(\set_property, \curve, self.model.curve);
		self.changed(\set_property, \curve_amps, self.model.curve_amps);
	},

	get_buffer: { arg self;
		self.buffer;
	},

	get_val: { arg self;
		self.model.val;
	},
);

~class_pparam_wavetable_controller = (
	model: (
		name: "Wt",
		uname: \osc1_wt,
		kind: \wavetable,
		transmit: \wavetable,
		buffer_range: 0,
		val: 0
	),

	new: { arg self, controller, paramdata;
		self = self.deepCopy;
		self.main_controller = { arg self; controller };
		self.menu_items = controller.get_wavetables;
		self.model.putAll(paramdata);
		self.buffer = Buffer.alloc(s, 1024, 1);
		controller.register_buffer(self.buffer, self.model.uname);
		self.curvebank = ~curvebank;
		self.set_curve(self.model.val);

		self;
	},

	save_data: { arg self;
		self.model;
	},

	load_data: { arg self, data;
		self.model.val = self.menu_items.detectIndex { arg item; item == data.val_uname };
		self.model.val_uname = data.val_uname;
		self.model.pathlist = data.pathlist;
		self.set_curve(self.model.val, true)
	},

	get_menu_items_names: { arg self;
		self.menu_items
	},

	set_curve: { arg self, curve_idx, load=false;
		var curve = self.menu_items[curve_idx];
		var apply_action, cancel_action;
		var was_custom = false;
		var osc_pos_ctrl;
		if(curve == \custom) {
			apply_action = { arg pathlist;
				self.model.pathlist = pathlist;
				pathlist.debug("class_pparam_wavetable_controller: set_curve: custom: pathlist");
				self.buffer_array.do(_.free);
				self.buffer_array = Buffer.allocConsecutive(pathlist.size, s, 2048);
				self.buffer_array.do { arg buf, idx;
					self.main_controller.register_buffer(buf, self.model.uname);
					~load_sample_in_wavetable_buffer.(buf, pathlist[idx].fullPath);
				};
				self.model.buffer_range = self.buffer_array.size-1;
				osc_pos_ctrl = self.main_controller.get_arg("osc%_wt_pos".format(self.model.indexes).asSymbol);
				osc_pos_ctrl.debug("osc_wt_pos ctrl");
				osc_pos_ctrl.model.spec.maxval = self.model.buffer_range - 0.001;
				self.model.val_uname = curve;
				self.model.val = curve_idx;
				self.main_controller.update_arg(self.model.uname);
			};
			cancel_action = {
				self.changed(\set_property, \value, self.model.val);
			};
			if(load) {
				apply_action.(self.model.pathlist);
				self.changed(\set_property, \value, self.model.val);
			} {
				~class_load_wavetable_dialog.new(apply_action, cancel_action);
			}
		} {
			if(self.model.val_uname == \custom) {
				was_custom = true;
			}; 
			~load_curve_in_wavetable_buffer.(self.buffer, self.curvebank[curve]);
			self.model.val_uname = curve;
			self.model.val = curve_idx;

			self.model.buffer_range = 0;
			self.main_controller.get_arg("osc%_wt_pos".format(self.model.indexes).asSymbol).model.spec.maxval(self.model.buffer_range);
			if(was_custom) {
				self.main_controller.update_arg(self.model.uname);
			}
		};
	},

	set_property: { arg self, name, val, update=true;
		switch(name,
			\label, { self.name = val },
			\value, { 
				self.set_curve(val)
			}
		);
		if(update) {
			self.changed(\set_property, name, val);
		}
	},

	refresh: { arg self;
		"wtREFRESH++".debug;
		self.changed(\set_property, \label, self.model.name);
		"wtREFRESH++ 2".debug;
		self.changed(\set_property, \value, self.model.val);
		"wtREFRESH++ 3".debug;

	},

	get_buffer: { arg self;
		if(self.model.val_uname == \custom) {
			self.buffer_array[0]
		} {
			self.buffer;
		}
	},

	get_val: { arg self;
		self.model.val;
	}
);

~class_pparam_kind_controller = (
	model: (
		name: "Filter kind",
		uname: \filter1_kind,
		kind: \filter_kind,
		transmit: \kind,
		val_uname: \bitcrusher,
		val: 0
	),

	save_data: { arg self;
		self.model;
	},

	load_data: { arg self, data;
		self.model.val = self.menu_items.detectIndex { arg item; item.uname == data.val_uname };
		self.model.val_uname = data.val_uname;
	},

	new: { arg self, controller, paramdata;
		self = self.deepCopy;
		self.main_controller = { arg self; controller };
		paramdata.debug("class_pparam_kind_controller");
		self.model.putAll(paramdata);

		self.menu_items = controller.get_module_variants(paramdata.bank);
		self.menu_items.debug("class_pparam_kind_controller: new: menu_items");
		self.model.val_uname = self.menu_items[self.model.val].uname;
		self.model.knobs.do { arg knobname;
			self.main_controller.get_arg(knobname).set_variant(self.menu_items[self.model.val])
		};

		self;
	},

	get_menu_items_names: { arg self;
		self.menu_items.collect { arg item;
			item.name
		}
	},

	set_property: { arg self, name, val, update=true;
		var final_val, mod;
		[name, val].debug("class_pparam_kind_controller");
		switch(name,
			\label, { self.name = val },
			\value, { 
				self.model.val = val;
				self.model.val_uname = self.menu_items[val].uname;
				self.model.knobs.do { arg knobname;
					self.main_controller.get_arg(knobname).set_variant(self.menu_items[val])
				};
				self.main_controller.update_arg(self.model.uname);
			}
		);
		if(update) {
			self.changed(\set_property, name, val);
		}
	},

	refresh: { arg self;
		"filkindREFRESH++".debug;
		self.changed(\set_property, \label, self.model.name);
		"filkindREFRESH++2".debug;
		self.changed(\set_property, \value, self.model.val);
		"filkindREFRESH++3".debug;

	},

	get_val: { arg self;
		self.model.val;
	}
);

~class_pparam_modmatrix_controller = (
	model: (
		name: "Modmatrix",
		uname: \modosc_matrix,
		kind: \matrix,
		routing_name: \modosc,
		transmit: \routing,
		selected_modkind: 0,
		val: (
			ring: 0,
			phase: 2,
			position: 0,
			filterfm: 0
		)
	),

	save_data: { arg self;
		self.model;
	},

	load_data: { arg self, data;
		self.model.val = data.val;
		self.model.selected_modkind = data.selected_modkind;
	},

	new: { arg self, controller, paramdata;
		self = self.deepCopy;
		self.main_controller = { arg self; controller };
		paramdata.debug("class_pparam_kind_controller");
		self.model.kind = paramdata.kind;
		self.model.uname = paramdata.uname;
		self.model.name = paramdata.name;

		self;
	},

	set_property: { arg self, name, val, update=true;
		var final_val, mod;
		[name, val].debug("class_pparam_modmatrix_controller");
		switch(name,
			\label, { self.name = val },
			\modvalue, { 
				// val: [modtype, oscnum]
				self.model.val[val[0]] = val[1];
				self.main_controller.update_arg(self.model.uname);
			},
			\selected_modkind, {
				self.model.selected_modkind = val;
			},
			\value, {
				self.model.val = val;
				self.main_controller.update_arg(self.model.uname);
			}
		);
		if(update) {
			self.changed(\set_property, name, val);
		}
	},

	refresh: { arg self;
		self.changed(\set_property, \value, self.model.val);
		self.changed(\set_property, \selected_modkind, self.model.selected_modkind);

	},

	get_val: { arg self;
		self.model.val;
	}
);

~class_pparam_mute_controller = (
	model: (
		name: "Osc2",
		uname: \osc1,
		kind: \mute,
		transmit: \mute,
		val: 0		// FIXME: 0 is On, should be off
	),

	new: { arg self, controller, paramdata;
		self = self.deepCopy;
		self.main_controller = { arg self; controller };
		self.model.putAll(paramdata);

		self;
	},

	save_data: { arg self;
		self.model;
	},

	load_data: { arg self, data;
		self.model.val = data.val;
	},

	set_property: { arg self, name, val, update=true;
		var final_val, mod;
		switch(name,
			\label, { self.name = val },
			\value, { 
				self.model.val = val;
				self.main_controller.update_arg(self.model.uname);
			}
		);
		if(update) {
			self.changed(\set_property, name, val);
		}
	},

	refresh: { arg self;
		"muteREFRESH++".debug;
		self.changed(\set_property, \label, self.model.name);
		"muteREFRESH++2".debug;
		self.changed(\set_property, \value, self.model.val);
		"muteREFRESH++3".debug;

	},

	get_val: { arg self;
		self.model.val;
	}
);

~class_pparam_steps_controller = (
	model: (
		name: "Steps",
		kind: \steps,
		transmit: \steps,
		uname: \modulator1_steps1,
		indexes: [0,0],
		val: 1 ! 16,
	),

	new: { arg self, controller, paramdata;
		self = self.deepCopy;
		self.bus = Bus.control(s, 1);
		self.main_controller = { arg self; controller };
		self.model.putAll(paramdata);

		self;
	},

	save_data: { arg self;
		self.model;
	},

	load_data: { arg self, data;
		self.model.val = data.val;
	},

	set_property: { arg self, name, val, update=true;
		var final_val, mod;
		[self.model.uname, name, val, update].debug("class_pparam_steps_controller.set_property");
		switch(name,
			\value, { 
				self.model.val = val;
				self.main_controller.update_arg(self.model.uname);
			}
		);
		if(update) {
			self.changed(\set_property, name, val);
		}
	},

	refresh: { arg self;
		"REFRESH++".debug;
		self.changed(\set_property, \value, self.model.val);
	},

	get_val: { arg self;
		self.model.val;
	},
);

~class_pparam_controller = (
	model: (
		name: "Pitch",
		kind: \knob3,
		transmit: \bus,
		uname: \osc1_freq,
		val: 1200,
		norm_val: 0.7,
		spec: \freq.asSpec
	),

	new: { arg self, controller, paramdata;
		self = self.deepCopy;
		self.main_controller = { arg self; controller };
		self.model.putAll(paramdata);
		self.bus = controller.get_new_control_bus(self.model.uname);
		self.set_val(paramdata.val ?? self.model.spec.default);

		self;
	},

	save_data: { arg self;
		self.model;
	},

	load_data: { arg self, data;
		self.set_val(data.norm_val, true);
	},

	set_val: { arg self, val, norm=false;
	
		if(norm) {
			self.model.norm_val = val;
			self.model.val = self.model.spec.map(val);
		} {
			self.model.val = val;
			self.model.norm_val = self.model.spec.unmap(val);
		};

		"before final_val".debug;
		[self.model.uname, self.main_controller.modulation_manager.get_external_value(self.model.uname)].debug("ext");

		self.update_val;

	},

	update_val: { arg self;
		var final_val;
		self.model.uname.debug("class_pparam_controller: update_val");
		final_val = self.model.norm_val + self.main_controller.modulation_manager.get_external_value(self.model.uname);
		self.bus.set(self.model.spec.map(final_val));
	},

	set_property: { arg self, name, val, update=true;
		var mod;
		[name, val, update].debug("class_pparam_controller.set_property");
		switch(name,
			\label, { self.name = val },
			\range, {
				// val: [slot_idx, modrange]
				self.main_controller.modulation_manager.set_range(self.model.uname, val[0], val[1]);
			},
			\update_range, {
				self.main_controller.update_arg(self.model.uname);
			},
			\modulation_source, {
				// val: [slot_idx, modsource_kind, modsource_id]
				if(val[1].isNil) {
					self.main_controller.modulation_manager.unbind([self.model.uname, val[0]]);
				} {
					self.main_controller.modulation_manager.bind([val[1],val[2]], [self.model.uname, val[0]]);
				};
				self.main_controller.update_arg(self.model.uname);

			},
			\value, { 
				self.set_val(val, true);
			}
		);
		if(update) {
			self.changed(\set_property, name, val);
		}
	},

	refresh: { arg self;
		"REFRESH++".debug;
		self.changed(\set_property, \label, self.model.name);
		self.changed(\set_property, \value, self.model.val);
		self.model.numslot.do { arg idx;
			var range = self.main_controller.modulation_manager.get_range(self.model.uname, idx);
			self.changed(\set_property, \range, [idx, range]);
		};
		self.model.numslot.do { arg idx;
			self.changed(\set_property, \modulation_source, 
				self.main_controller.modulation_manager.get_source(self.model.uname, idx)
			);
		};

	},

	get_bus: { arg self;
		self.bus;
	},

	get_val: { arg self;
		self.model.val;
	},

	get_norm_val: { arg self;
		self.model.norm_val;
	}

);

~class_pparam_macro_controller = (
	parent: ~class_pparam_controller,

	set_val: { arg self, val;
		var final_val;
	
		self.model.val = val;
		self.model.norm_val = val;
		final_val = self.main_controller.modulation_manager.set_external_value([\macro, self.model.arg_index], val);
	},


);

~class_pparam_multi_controller = (
	parent: ~class_pparam_controller,
	new: { arg self, controller, paramdata;
		self = self.parent[\new].(self, controller, paramdata);
		self.model.arg_index = paramdata.arg_index;
		self;
	},

	set_variant: { arg self, variant;
		self.model.name = variant.args[self.model.arg_index] ?? "N/A";
		self.model.spec = if(variant.specs.notNil and: { variant.specs[self.model.arg_index].notNil }) {
			variant.specs[self.model.arg_index].asSpec;
		} {
			\freq.asSpec;
		};
		self.set_val(self.model.norm_val, true);
		self.refresh;
	}
);

~class_presets_global_controller = (

	model: (
		name: "Presets list",
		uname: \saveload_presets,
		kind: \preset,
		transmit: \none,
		val_uname: \bitcrusher,
		val: 0
	),

	save_data: { arg self;
	},

	load_data: { arg self, data;
	},

	new: { arg self, controller, paramdata;
		self = self.deepCopy;
		self.main_controller = { arg self; controller };
		paramdata.debug("class_pparam_kind_controller");
		self.model.putAll(paramdata);

		self.model.val_uname = \no_preset;
		self.model.val = 0;
		self.preset_dir = Platform.userAppSupportDir +/+ "passive/presets/";
		self.preset_dir.mkdir;

		self.read_presets;

		self;
	},

	get_menu_items_names: { arg self;
		self.menu_items
	},

	next_preset: { arg self;
		var val;
		val = (self.model.val + 1).clip(0, self.menu_items.size-1);
		if(val != self.model.val) {
			self.model.val = val;
			self.set_property(\value, self.model.val);
		}
	},

	previous_preset: { arg self;
		var val;
		val = (self.model.val - 1).clip(0, self.menu_items.size-1);
		if(val != self.model.val) {
			self.model.val = val;
			self.set_property(\value, self.model.val);
		}
	},

	save_current_preset_as_uname: { arg self, uname;
		var preset;
		uname.debug("class_presets_global_controller: save_current_preset_as_uname");
		if(uname != \no_preset) {
			uname.dump;
			self.menu_items.do { arg me; me.dump };
			preset = self.main_controller.save_preset;
			preset[\uname] = uname;
			preset[\name] = uname; // TODO: watch for forbiden chars in path
			preset.writeArchive(self.preset_dir +/+ uname);
			self.read_presets;
			[self.menu_items, uname, self.menu_items.indexOfEqual(uname)].debug("items, uname, index");
			self.model.val = self.menu_items.indexOfEqual(uname) ?? 0;
			self.changed(\set_property, \value, self.model.val);
		};
	},

	load_preset_by_uname: { arg self, uname;
		if(uname != \no_preset) {
			self.main_controller.load_preset(self.preset_dict[uname]);
		};
	},

	read_presets: { arg self;
		self.preset_dict = Dictionary.new;
		PathName.new(self.preset_dir).filesDo { arg file;
			var preset;
			preset = Object.readArchive(file.fullPath);
			self.preset_dict[preset.uname] = preset;
		};
		self.menu_items = [\no_preset] ++ self.preset_dict.keys.asList.sort;
		self.changed(\set_property, \menu_items);
	},

	set_property: { arg self, name, val, update=true;
		var final_val, mod;
		[name, val].debug("class_presets_global_controller");
		switch(name,
			\value, { 
				self.model.val = val;
				self.model.val_uname = self.menu_items[val];
				self.load_preset_by_uname(self.model.val_uname);
			}
		);
		if(update) {
			self.changed(\set_property, name, val);
		}
	},

	refresh: { arg self;
		self.changed(\set_property, \value, self.model.val);

	},

	get_val: { arg self;
		self.model.val;
	}
);

~class_voicing_controller = (
	model: (
		name: "Voicing",
		uname: \voicing,
		kind: \voicing,
		transmit: \voicing,

		unisono:0,
		pitch_lorange:0,
		pitch_hirange:1,
		wavetable_lorange: 0,
		wavetable_hirange: 1,

		pitch_chord: true,

		enable_pan_spreading: true,
		enable_pitch_spreading: true,
		enable_wavetable_spreading: true,
	),

	save_data: { arg self;
	},

	load_data: { arg self, data;
	},

	new: { arg self, controller, paramdata;
		self = self.deepCopy;
		self.main_controller = { arg self; controller };
		paramdata.debug("class_voicing_controller");
		self.model.putAll(paramdata);

		self;
	},

);

////////////////////// main controllers

~class_modulation_manager = (
	source_dict: Dictionary.new,
	slot_dict: Dictionary.new,
	modulation_dict: Dictionary.new,
	external_dict: Dictionary.new, // dest_uname -> List of slot index controlled by external source

	new: { arg self, main_controller;
		self = self.deepCopy;

		self.main_controller = { arg self; main_controller };

		self;
	},

	get_source: { arg self, uname, idx;
		// format is from set_property:\modulation_source
		if(self.slot_dict[[uname, idx]].isNil) {
			// no modulation for this param
			[idx]
		} {
			[idx] ++ self.slot_dict[[uname, idx]]
		}
	},

	bind: { arg self, source, dest;
		// source: [\macro, 1], dest: [\osc1_amp, 0]
		self.unbind(dest);
		if( self.source_dict[source].isNil ) {
			self.source_dict[source] = Set[dest];
		} {
			self.source_dict[source].add(dest)
		};
		self.slot_dict[dest] = source;
		if([\macro, \midi].includes(source[0])) {
			if( self.external_dict[dest[0]].isNil ) {
				self.external_dict[dest[0]] = Set.new;
			};
			self.external_dict[dest[0]].add(dest[1]);
		} {
			self.external_dict[dest[0]].remove(dest[1]);
		}
	},

	unbind: { arg self, dest;
		var source;
		source = self.slot_dict[dest];
		self.slot_dict[dest] = nil;
		if(source.notNil) {
			self.source_dict[source].remove(dest);
		};
		self.external_dict[dest[0]].remove(dest[1]);
	},

	make_modulation: { arg self, uname, idx; 		// private
		self.modulation_dict[[uname, idx]] = (
			range: 0,
			muted: false
		);
	},

	get_range: { arg self, uname, idx;
		if(self.modulation_dict[[uname, idx]].notNil) {
			self.modulation_dict[[uname, idx]].range;
		} {
			0
		}
	},

	set_range: { arg self, uname, idx, val;
		if(self.modulation_dict[[uname, idx]].isNil) {
			self.make_modulation(uname, idx);
		};
		self.modulation_dict[[uname, idx]].range = val;
	},

	set_muted: { arg self, uname, idx, val=true;
		if(self.modulation_dict[[uname, idx]].isNil) {
			self.make_modulation(uname, idx);
		};
		self.modulation_dict[[uname, idx]].muted = val;
	},

	get_label: { arg self, uname, idx;
		if(self.slot_dict[[uname, idx]].isNil) {
			""
		} {
			switch(self.slot_dict[[uname, idx]][0],
				\mod, {
					self.slot_dict[[uname, idx]][0].asString;
				},
				\macro, {
					"a"
				},
				\midi, {
					"i"
				}
			)
		}
	},

	get_external_value: { arg self, dest_uname;
		// dest_uname -> [source_val]
		//external_dict contains indexes of external sources
		var source;
		var ret = 0;
		self.external_dict[dest_uname].do { arg idx;
			source = self.slot_dict[[dest_uname, idx]];
			ret = ret + (self.modulation_dict[source].val * self.get_range(dest_uname, idx));
		};
		ret;
	},

	set_external_value: { arg self, source, val;
		[source, val].debug("modulation_manager.set_external_value");
		if(self.modulation_dict[source].isNil) {
			self.modulation_dict[source] = ();
		};
		self.modulation_dict[source].val = val;
		[self.source_dict].debug("modulation_manager.set_external_value: source_dict");
		self.source_dict[source].do { arg dest;
			self.main_controller.get_arg(dest[0]).update_val;
		}
	},

	save_data: { arg self;
		var ret = Dictionary.new;
		#[slot_dict, modulation_dict, source_dict, external_dict].do { arg key;
			ret[key] = self[key]
		};
		ret;
	},

	load_data: { arg self, data;
		#[slot_dict, modulation_dict, source_dict, external_dict].do { arg key;
			self[key] = data[key]
		};
	},

	get_instr_modulation: { arg self;
		var mod = Dictionary.new;
		self.slot_dict.debug("modulation_manager: get_instr_modulation: slot_dict");
		self.source_dict.debug("modulation_manager: get_instr_modulation: source_dict");
		self.modulation_dict.debug("modulation_manager: get_instr_modulation: modulation_dict");
		self.external_dict.debug("modulation_manager: get_instr_modulation: external_dict");
		self.slot_dict.keysValuesDo { arg dest, source;
			var uname, idx;
			var srckind, srcidx;
			var ret_source, ret_range, ret_norm_range, ret_spec, muted;
			# uname, idx = dest;
			# srckind, srcidx = source;
			if(srckind == \mod) {
				[dest, source].debug("srckindmod");
				ret_source = srcidx;
				if(self.modulation_dict[dest].notNil) {
					[dest, source].debug("modulnotnil");
					if(self.modulation_dict[dest].muted.not) {
						var ctrl;
						[dest, source].debug("modulnotmuted");
						ctrl = self.main_controller.get_arg(uname);
						ret_range = self.modulation_dict[dest].range;
						ret_range.debug("ret_range");
						ret_range = ret_range.clip(ctrl.get_norm_val.neg, 1 - ctrl.get_norm_val);
						ret_range.debug("ret_range2");
						[ctrl.get_norm_val, ctrl.model.spec.range, ret_range].debug("normval, specrange, retrange2");
						//ret_range = ctrl.model.spec.map(ret_range.abs) * sign(ret_range);
						ret_norm_range = ret_range;
						ret_spec = ctrl.model.spec;
						ret_range = ctrl.model.spec.range * ret_range;
						ret_range.debug("ret_range3");
					}
				}
			}; 
			if(ret_source.notNil) {
				if(mod[uname].isNil) {
					mod[uname] = Dictionary.new
				};
				mod[uname][idx] = (
					source: ret_source,
					range: ret_range,
					norm_range: ret_norm_range,
				);
				mod[uname][\spec] = ret_spec;
			}
		};
		mod;

	}
);


~make_midi_note_responder = { arg player;
	var prec;

	prec = (
		nonr: nil,
		noffr: nil,
		livebook: Dictionary.new,

		start_liveplay: { arg self;
			var livesynth;
			livesynth = player.get_piano;
			self.livebook = Dictionary.new;
			NoteOnResponder.removeAll;
			NoteOffResponder.removeAll;

			self.nonr = NoteOnResponder { arg src, chan, num, veloc;

				self.livebook[[chan, num]] = livesynth.value(num.midicps, veloc/127);
				[src, chan, num, veloc].debug("note on");
			};
			self.noffr = NoteOffResponder { arg src, chan, num, veloc;
				var note;
				[self.livebook[[chan,num]], [src, chan, num, veloc]].debug("note off");
				self.livebook[[chan,num]].release_node;
			};
		},

		stop_liveplay: { arg self;
			"".debug("end liveplay: NOW");
			self.nonr.remove;
			self.noffr.remove;
			self.livebook.keysValuesDo { arg k, v; v.release }; // free last synths
		}
	);
	prec;

};

~class_passive_controller = (
	
	bus_dict: Dictionary.new,
	rebuild_synthdef: true,

	new: { arg self;
		self = self.deepCopy;
		self.modulation_manager = ~class_modulation_manager.new(self);
		self.make_params;
		self.fx_feedback_bus = self.get_new_control_bus(\fx_fb);
		self.fx_bus = self.get_new_audio_bus(\fx, 2);
		self.fx_bypass_bus = self.get_new_audio_bus(\fx_bypass, 2);
		self.modulation_fxbus = 8.collect { arg idx;
			self.get_new_audio_bus((\modfxbus++idx).asSymbol, 1);
		};

		self;
	},

	make_params: { arg self;
		self.data = Dictionary.new;

		//////// creating controllers

		self.module_kinds = ~make_module_kinds.();
		self.paramdata = ~make_param_data.();

		self.paramdata.keys.do { arg frame;
			self.paramdata[frame].do { arg param;
				self.data[param.uname] = ~make_pparam_controller_from_kind.(self, param);
			}
		};

	},

	get_wavetables: { arg self;
		[
			\sin1,
			\square1,
			\saw1,
			\custom,
		]
	},

	get_curvebank: { arg self;
		~curvebank;
	},

	get_module_variants: { arg self, bank;
		self.module_kinds[bank];
	},

	build_synthdef: { arg self;
		var args = ();
		var synthdef;

		"passive: build_synthdef".debug;

		args[\routing] = (voicing:());
		args[\kinds] = ();
		args[\enabled] = ();
		args[\steps] = Array.newClear(4);


		self.data.keysValuesDo { arg key, val;
			var uname, ctrl;
			var kind;
			//val.model.uname.debug("build_synthdef: uname");
			switch(val.model.transmit,
				\bus, {
					args[val.model.uname] = val.get_bus.asMap;
					//args[val.model.uname] = val.get_bus;
				},
				\curve, {
					//val.debug("class_passive_controller: build_synthdef: curve: ctrl");
					kind = switch(val.model.kind,
						\curve, { \lfo },
						\perfcurve, { \performer }
					);
					ctrl = self.get_arg(("modulator%_kind".format(val.model.indexes[0]+1)).asSymbol);
					//["modulator%_kind".format(val.model.indexes[0]+1), kind, ctrl.model.val_uname, ctrl].debug("modulator kind");
					if(ctrl.model.val_uname == kind ) {
						uname = "modulator%_curve%".format(val.model.indexes[0]+1, val.model.indexes[1]+1);
						args[uname.asSymbol] = val.get_buffer.bufnum ?? 0;
						//[args[uname.asSymbol], uname.asSymbol, val.get_buffer.bufnum].debug("bufnum");
					}
				},
				\wavetable, {
					args[val.model.uname] = val.get_buffer.bufnum;
					args[\kinds][val.model.uname] = val.model.buffer_range;
				},
				\routing, {
					args[\routing][val.model.routing_name] = val.model.val_uname ?? val.model.val;
				},
				\voicing, {
					uname = val.model.uname.asString.replace("voicing_", "").asSymbol;
					uname.debug("build: uname");
					args[\routing][\voicing][uname] = switch(val.model.kind,
						\knob, { val.model.val },
						\mute, { val.model.val == 0 }
					);
				},
				\kind, {
					uname = val.model.uname.asString.replace("_kind", "").asSymbol;
					args[\kinds][uname] = val.model.val_uname;
				},
				\steps, {
					if(args[\steps][val.model.indexes[0]].isNil) {
						args[\steps][val.model.indexes[0]] = ();
					};
					args[\steps][val.model.indexes[0]][val.model.indexes[1]] = val.model.val;
				},
				\mute, {
					args[\enabled][val.model.uname] = (val.model.val == 0);
				}
			);
		};
		args[\mod] = self.modulation_manager.get_instr_modulation;
		args[\routing][\fx_feedback_bus] = self.fx_feedback_bus.index; // FIXME: must not be shared bus until fx are separated
		args[\routing][\modulation_fxbus] = self.modulation_fxbus.collect(_.index); // FIXME: must not be shared bus until fx are separated

		//synthdef = Instr(\passive).asSynthDef( args );
		//self.synthdef = synthdef;
		//synthdef.add;
		//self.synthdef.debug("synthdef");
		args[\gate] = 1;
		{
			var enabled, kinds, mod, routing, steps;

			enabled = args[\enabled];
			kinds = args[\kinds];
			mod = args[\mod];
			routing = args[\routing];
			steps = args[\steps];
			self.synthdef_ns_args = [enabled, kinds, mod, routing, steps];

			#[ enabled, kinds, mod, routing, steps ].do { arg key; args.removeAt(key) };

			SynthDef(\passive, { 
					var ou;
					ou = SynthDef.wrap(Instr(\passive).func, nil, [enabled, kinds, mod, routing, steps]);
					Out.ar(self.fx_bus.index, ou[0]);
					Out.ar(self.fx_bypass_bus.index, ou[1]);
					//Out.ar(0, ou);
			}).add;

			SynthDef(\passive_fx, { arg out = 0;
					var ou;
					var in = In.ar(self.fx_bus.index, 2);
					var in_bypass = In.ar(self.fx_bypass_bus.index, 2);
					ou = SynthDef.wrap(Instr(\passive_fx).func, nil, [in, in_bypass, enabled, kinds, mod, routing, steps]);
					ou = Out.ar(out, ou);
			}).add;
		}.value;
		self.synthdef_args = args;
		//self.patch = Patch(\passive,  self.synthdef_args );
		//self.patch.invalidateSynthDef;
		//self.rebuild = false;
		args.postcs;
	},

	update_arg: { arg self, uname;
		// TODO
		uname.debug("class_passive_controller: update_arg");
		self.build_synthdef;
		self.rebuild_synthdef = true;
	
	},

	get_piano: { arg self;
		{ arg freq=400, velocity=0.5;
			var synth;
			var busses;
			"###########----- making note".debug;
			self.synthdef_args[\freq] = freq;
			self.synthdef_args[\velocity] = velocity;
			//p = Patch(\passive,  self.synthdef_args );
			s.makeBundle(nil, {
				busses = 8.collect { arg idx;
					Bus.audio(s, 1);
				};
				self.synthdef_args[\modulation_bus] = busses;
				if(self.note_group.isNil or: {self.note_group.isPlaying.not}) {
					"########### making group".debug;
					self.note_group = Group.new(s);
					self.note_group.register;
				};

				"########### making synth".debug;
				synth = Synth(\passive, self.synthdef_args.getPairs, self.note_group);

				if(self.fx_node.isNil or: {self.fx_node.isPlaying.not or:{self.rebuild_synthdef}}) {
					"########### making fx".debug;
					self.fx_node.release;
					self.fx_node = Synth(\passive_fx, self.synthdef_args.getPairs, self.note_group, \addAfter);
					self.fx_node.register;
					self.rebuild_synthdef = false;
				}
			});
			//if(self.rebuild_synthdef) {
			//	debug("class_passive_controller: rebuild_synthdef");
			//	p.invalidateSynthDef;
			//	self.rebuild_synthdef = false;
			//};
			//p.play;
			(
				synth_node: synth,
				busses: busses,
				release_node: { arg self;
					"********************* releasing synth".debug;
					synth.release;
					self.busses.do { arg bus; bus.free; }
				};
			
			);
		}
	
	},

	make_midi_responder: { arg self;
		self.midi_note_responder = ~make_midi_note_responder.(self);
		self.midi_note_responder.start_liveplay;
		self.midi_note_responder;
	},

	get_new_control_bus: { arg self, uname;
		var bus;
		bus = Bus.control(s, 1);
		self.bus_dict[uname] = bus;
		bus
	},

	get_new_audio_bus: { arg self, uname, chan;
		var bus;
		bus = Bus.audio(s, chan);
		self.bus_dict[uname] = bus;
		bus
	},

	register_buffer: { arg self, buffer, uname;
		BufferPool.retain(buffer, \passive, uname);
	},

	destructor: { arg self;
		if(self.view.notNil) {
			self.view.window.close;
		};

		self.bus_dict.values.do { arg bus;
			bus.free;
		};

		BufferPool.release_client(\passive);
	},

	get_arg: { arg self, key;
		self.data[key]
	},

	get_mod_arg: { arg self, index, name;
		index = index+1;
		self.data[("modulator"++index++"_"++name).asSymbol]
	},

	get_env_arg: { arg self, index, name;
		index = index+1;
		self.data[("env"++index++"_"++name).asSymbol]
	},

	save_preset: { arg self;
		var ret = Dictionary.new;
		var preset = ();
		preset[\modulation_manager] = self.modulation_manager.save_data;
		preset[\name] = "No name";
		preset[\uname] = \noname;
		self.data.keys.do { arg key;
			ret[key] = self.data[key].save_data;
		};
		preset[\data] = ret;
		preset;
	},

	load_preset: { arg self, preset;
		self.modulation_manager.load_data(preset[\modulation_manager]);
		preset.data.keys.do { arg key;
			self.data[key].load_data(preset.data[key]);
			self.data[key].refresh;
		}
	},

	make_gui: { arg self;
		self.view = ~class_passive_view.new(self);
	}
);

