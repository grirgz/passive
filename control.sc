~load_curve_in_buffer = { arg buffer, curvefunc;
	var size = buffer.numFrames;
	//size.debug("load_curve_in_buffer: size");
	//buffer.debug("buffer");
	buffer.loadCollection(FloatArray.fill(size, { arg i;
		curvefunc.(i/size)
	}),0, { 
		//"done".debug;
	})
};

~load_curve_in_wavetable_buffer = { arg buffer, curvefunc;
	var size = buffer.numFrames;
	var wt, sig;
	sig = Signal.newClear(size/2);
	sig.waveFill(curvefunc, 0, 1);

	//size.debug("load_curve_in_wavetable_buffer: size");
	//buffer.debug("buffer");
	buffer.loadCollection(sig.asWavetable,0, { 
		//"done".debug;
	});
};

~load_curvelist_in_buffer = { arg buffer, curvefunclist, curve_amps;
	var size = buffer.numFrames;
	var slicesize = (size/curvefunclist.size).asInteger;
	//size.debug("load_curvelist_in_buffer: size");
	//buffer.debug("buffer");
	curvefunclist.do { arg curvefunc, idx;
		buffer.loadCollection(FloatArray.fill(slicesize, { arg i;
			curvefunc.(i/slicesize).linlin(-1,1,0,1) * curve_amps.wrapAt(idx)
		}), idx*slicesize, { 
			//"done".debug;
		})
	}
};

~load_sample_as_signal = { arg path;
	var file, sig;


	file = SoundFile.openRead(path);

	sig = Signal.newClear(file.numFrames);
	file.readData(sig);
	file.close; // close the file
	sig;
};

~load_sample_in_wavetable_buffer = { arg buffer, path;
	var sig;
	sig = ~load_sample_as_signal.(path);
	~load_signal_in_wavetable_buffer.(buffer, sig);

};

~load_sample_as_sigfunc = { arg path;
	var sig;
	sig = ~load_sample_as_signal.(path);
	{ arg x; 
		x = x % 1;
		sig[ x * sig.size ]
	};

};


~load_signal_in_wavetable_buffer = { arg buffer, sig;
	var size, fsize;
	// resamp the table to have a pow of 2 (bigger to avoid aliassing)
	// if u read many diff samples choose a bigger pow of 2
	size = buffer.numFrames;
	fsize = sig.size;
	//[fsize, size/2].debug("load_sample_in_wavetable_buffer: resampling");
	sig = sig.resamp1(size/2).as(Signal);

	// Convert it to a Wavetable
	sig = sig.asWavetable;

	buffer.loadCollection(sig);

};



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
		\static_knob, { ~class_pparam_static_controller.new(main_controller, paramdata) },
		\spec_knob, { ~class_pparam_spec_controller.new(main_controller, paramdata) },
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
		\ktrcurve, { ~class_ktrcurve_controller.new(main_controller, paramdata) },
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
		self.buffer = Buffer.alloc(s, controller.get_config.lfo_buffer_size, 1);
		controller.register_buffer(self.buffer, self.model.uname);
		self.set_curve(self.model.curve);

		self;
	},

	save_data: { arg self;
		var data = self.model.deepCopy;
		if(self.model.custom_curve.notNil) {
			data.custom_curve = self.model.custom_curve.save_data;
		};
		data;
	},

	load_data: { arg self, data;
		self.model.curve = data.curve;
		if(data.custom_curve.notNil) {
			self.model.custom_curve = ~class_wavetable_file.new_from_data(data.custom_curve);
		};
		self.set_curve(self.model.curve, true);
	},

	get_menu_items_names: { arg self;
		self.get_curvebank.get_keys(self.get_curvebank) ++ [\custom];
	},

	get_curvebank: { arg self;
		self.main_controller.get_curvebank;
	},

	set_curve: { arg self, val, load=false;
		var curvefun;
		var apply_action, cancel_action;
		if(val == \custom) {

			apply_action = { arg pathlist;
				self.model.custom_curve = pathlist[0];
				self.model.custom_curve.load_in_wavetable_buffer(self.buffer);
				self.model.curve = val;
				self.custom_sigfunc = self.model.custom_curve.as_sigfunc;
				self.changed(\set_property, \curve, self.model.curve);
			};

			cancel_action = {
				self.changed(\set_property, \curve, self.model.curve);
			};

			if(load) {
				apply_action.(self.model.pathlist);
				self.changed(\set_property, \curve, self.model.curve);
			} {
				~class_load_wavetable_dialog.new(apply_action, cancel_action, nil, true);
			}
		} {
			curvefun = self.main_controller.get_curvebank[val];
			if(curvefun.notNil) {
				~load_curve_in_wavetable_buffer.(self.buffer, curvefun);
				self.model.curve = val;
			} {
				val.debug("class_pparam_curve_controller: curve not valid");
			}
		};
	},

	get_sigfunc: { arg self;
		if(self.model.curve == \custom) {
			self[\custom_sigfunc];
		} {
			self.get_curvebank[self.model.curve];
		}
	},

	set_property: { arg self, name, val, update=true;
		//[name, val, update].debug("class_pparam_controller.set_property");
		switch(name,
			\curve, { 
				self.set_curve(val)
			},
		);
		if(update) {
			self.changed(\set_property, name, val);
		}
	},

	refresh: { arg self;
		//"REFRESH++".debug;
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
		self.buffer = Buffer.alloc(s, controller.get_config.performer_buffer_size, 1);
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
		//[name, val, update].debug("class_pparam_controller.set_property");
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
		//"REFRESH++".debug;
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
		self.menu_items = controller.get_curvebank[\get_keys].(controller.get_curvebank) ++ [\custom];
		self.model.putAll(paramdata);
		self.buffer = Buffer.alloc(s, controller.get_config.wavetable_buffer_size, 1);
		controller.register_buffer(self.buffer, self.model.uname);
		self.curvebank = ~curvebank;
		self.set_curve(self.model.val);

		self;
	},

	save_data: { arg self;
		var data = self.model.deepCopy;
		data.pathlist = data.pathlist.collect(_.save_data);
	},

	load_data: { arg self, data;
		self.model.val = self.menu_items.detectIndex { arg item; item == data.val_uname };
		self.model.val_uname = data.val_uname;
		self.model.pathlist = data.pathlist.collect{ arg dat; ~class_wavetable_file.new_from_data(dat) };
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
				//pathlist.debug("class_pparam_wavetable_controller: set_curve: custom: pathlist");
				self.buffer_array.do(_.free);
				self.buffer_array = Buffer.allocConsecutive(pathlist.size, s, self.main_controller.get_config.wavetable_buffer_size);
				self.buffer_array.do { arg buf, idx;
					self.main_controller.register_buffer(buf, self.model.uname);
					pathlist[idx].load_in_wavetable_buffer(buf);
					//~load_sample_in_wavetable_buffer.(buf, pathlist[idx].fullPath);
				};
				self.model.buffer_range = self.buffer_array.size-1;
				osc_pos_ctrl = self.main_controller.get_arg("osc%_wt_pos".format(self.model.indexes).asSymbol);
				//osc_pos_ctrl.debug("osc_wt_pos ctrl");
				osc_pos_ctrl.model.spec.maxval = self.model.buffer_range - 0.0001;
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
		//"wtREFRESH++".debug;
		self.changed(\set_property, \label, self.model.name);
		//"wtREFRESH++ 2".debug;
		self.changed(\set_property, \value, self.model.val);
		//"wtREFRESH++ 3".debug;

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
		self.set_property(\value, self.model.val);
	},

	new: { arg self, controller, paramdata;
		self = self.deepCopy;
		self.main_controller = { arg self; controller };
		//paramdata.debug("class_pparam_kind_controller");
		self.model.putAll(paramdata);

		self.menu_items = controller.get_module_variants(paramdata.bank);
		//self.menu_items.debug("class_pparam_kind_controller: new: menu_items");
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
		//[name, val].debug("class_pparam_kind_controller");
		switch(name,
			\label, { self.name = val },
			\value, { 
				self.model.val = val;
				self.model.val_uname = self.menu_items[val].uname;
				self.model.knobs.do { arg knobname;
					//[knobname, val, self.menu_items[val]].debug("°°class_pparam_kind_controller: set_property: value: variant");
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
		//"filkindREFRESH++".debug;
		self.changed(\set_property, \label, self.model.name);
		//"filkindREFRESH++2".debug;
		self.changed(\set_property, \value, self.model.val);
		//"filkindREFRESH++3".debug;

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
		//paramdata.debug("class_pparam_kind_controller");
		self.model.kind = paramdata.kind;
		self.model.uname = paramdata.uname;
		self.model.name = paramdata.name;

		self;
	},

	set_property: { arg self, name, val, update=true;
		var final_val, mod;
		//[name, val].debug("class_pparam_modmatrix_controller");
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
		//"muteREFRESH++".debug;
		self.changed(\set_property, \label, self.model.name);
		//"muteREFRESH++2".debug;
		self.changed(\set_property, \value, self.model.val);
		//"muteREFRESH++3".debug;

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
		self.model.range = data.range;
	},

	set_property: { arg self, name, val, update=true;
		var final_val, mod;
		//[self.model.uname, name, val, update].debug("class_pparam_steps_controller.set_property");
		switch(name,
			\value, { 
				self.model.val = val;
				self.main_controller.update_arg(self.model.uname);
			},
			\range, { 
				//self.model.range.debug("old range");
				if(self.model.range != val) {
					self.model.range = val;
					self.main_controller.update_arg(self.model.uname);
				} {
					//"dont update range".debug;
				};
			}
		);
		if(update) {
			self.changed(\set_property, name, val);
		}
	},

	refresh: { arg self;
		//"REFRESH++".debug;
		self.changed(\set_property, \value, self.model.val);
		if(self.model.range.notNil) {
			self.changed(\set_property, \range, self.model.range);
		}
	},

	get_val: { arg self;
		self.model.val;
	},
);

///////////////////// simple control controllers

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

	set_transfert_function: { arg self, fun;
		
	
	},

	save_data: { arg self;
		self.model;
	},

	load_data: { arg self, data;
		self.set_val(data.norm_val, true);
	},

	set_val: { arg self, val, norm=false;
		//[self.model.uname, val, norm].debug("class_pparam_controller: set_val");
	
		if(norm) {
			self.model.norm_val = val;
			self.model.val = self.model.spec.map(val);
		} {
			self.model.val = val;
			self.model.norm_val = self.model.spec.unmap(val);
		};

		//"before final_val".debug;
		[self.model.uname, self.main_controller.modulation_manager.get_external_value(self.model.uname)].debug("ext");

		self.update_val;

	},

	update_val: { arg self;
		var final_val;
		self.model.uname.debug("class_pparam_controller: update_val");
		final_val = self.model.norm_val + self.main_controller.modulation_manager.get_external_value(self.model.uname);
		self.bus.set(self.model.spec.map(final_val));
	},

	is_slot_muted: { arg self, idx;
		self.main_controller.modulation_manager.is_muted(self.model.uname, idx);
	},

	get_slot_representation: { arg self, idx;
		// return [string, color]
		var modkind, modval;
		var color, string;
		var modman = self.main_controller.modulation_manager;
		#idx, modkind, modval = modman.get_source(self.model.uname, idx);

		if(modkind.isNil) {
			string = "";
		} {
			//FIXME: use other function to avoid code redondance
			if(modval.isInteger) {
				string = modval+1;
			} {
				string = modval.asString[0].asString;
			
			};
		};

		color = switch(modkind,
			\internal, { Color.magenta },
			\mod, { Color.red },
			\macro, { Color.green },
			\midi, { Color.blue },
			\special, { Color.blue },
			{ Color.black }
		);
		//[self.model.uname, idx, modman.is_muted(self.model.uname, idx)].debug("class_pparam_controller: get_slot_representation: is muted");
		if(modman.is_muted(self.model.uname, idx)) {
			color = Color.gray;
		};
		[string, color]
	},

	set_property: { arg self, name, val, update=true;
		var mod;
		//[name, val, update].debug("class_pparam_controller.set_property");
		switch(name,
			\label, { self.model.name = val },
			\range, {
				// val: [slot_idx, modrange]
				self.main_controller.modulation_manager.set_range(self.model.uname, val[0], val[1]);
			},
			\update_range, {
				self.main_controller.update_arg(self.model.uname);
			},
			\mute_slot, {
				self.main_controller.modulation_manager.set_muted(self.model.uname, val[0], val[1]);
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
		//"class_pparam_controller: REFRESH".debug;
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
		//"class_pparam_controller: end REFRESH".debug;

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

~class_pparam_static_controller = (
	parent: ~class_pparam_controller,

	new: { arg self, controller, paramdata;
		self = self.deepCopy;
		self.main_controller = { arg self; controller };
		self.model.putAll(paramdata);
		//self.bus = controller.get_new_control_bus(self.model.uname); // FIXME: should not have bus
		self.in_init = true;
		self.set_val(paramdata.val ?? self.model.spec.default);
		self.in_init = false;

		self;
	},

	update_val: { arg self;
		//self.model.uname.debug("class_pparam_fixed_controller: update_val");
		if(self.in_init.not) { // bug if update while all args are not initialized
			self.main_controller.update_arg(self.model.uname);
		};
	},


);

~class_pparam_spec_controller = (
	parent: ~class_pparam_controller,

	new: { arg self, controller, paramdata;
		self = self.deepCopy;
		self.main_controller = { arg self; controller };
		self.model.putAll(paramdata);
		self.in_init = true;
		//[paramdata.val, self.model.uname].debug("class_pparam_spec_controller: paramdata.val");
		self.set_val(paramdata.val ?? self.model.spec.default);
		//[self.model.val, self.model.uname].debug("class_pparam_spec_controller: model.val");
		self.in_init = false;

		self;
	},

	update_val: { arg self;
		var dest_param, spec;
		if(self.in_init.not) {
			dest_param = self.main_controller.get_arg(self.model.destination);
			//self.model.uname.debug("class_pparam_fixed_controller: update_val");
			spec = dest_param.model.spec.copy;
			if(self.model.spec_bound == \minval) {
				spec.minval = self.model.val;
				dest_param.model.spec = spec;
			} {
				spec.maxval = self.model.val;
				dest_param.model.spec = spec;
			};
			dest_param.set_val(dest_param.model.norm_val, true);
			dest_param.changed(\set_property, \value, dest_param.model.val);
		};
	},


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


/////////////////////// central panel controllers

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
		//paramdata.debug("class_pparam_kind_controller");
		self.model.putAll(paramdata);

		self.model.val_uname = \no_preset;
		self.model.val = 0;
		//self.preset_dir = Platform.userAppSupportDir +/+ "passive/presets/";
		self.preset_dir = controller.get_config.preset_path;
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
		//uname.debug("class_presets_global_controller: save_current_preset_as_uname");
		if(uname != \no_preset) {
			uname.dump;
			self.menu_items.do { arg me; me.dump };
			preset = self.main_controller.save_preset;
			preset[\uname] = uname;
			preset[\name] = uname; // TODO: watch for forbiden chars in path
			preset.writeArchive(self.preset_dir +/+ uname);
			self.read_presets;
			//[self.menu_items, uname, self.menu_items.indexOfEqual(uname)].debug("items, uname, index");
			self.model.val = self.menu_items.indexOfEqual(uname) ?? 0;
			self.changed(\set_property, \value, self.model.val);
		};
	},

	load_preset_by_uname: { arg self, uname;
		var preset;
		if(uname != \no_preset) {
			self.main_controller.synthdef_name_suffix = "_"++uname;
			preset = self.main_controller.load_preset(self.preset_dict[uname]);
			self.model.val = self.menu_items.indexOfEqual(uname) ?? 0;
			self.changed(\set_property, \value, self.model.val);
		} {
			self.main_controller.synthdef_name_suffix = "";
			nil;
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
		//[name, val].debug("class_presets_global_controller");
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

~class_ktrcurve_controller = (
	model: (
		name: "KtrOsc",
		kind: \ktrcurve,
		transmit: \none,
		//uname: \ktr_osc,
		curve: \off,
		//curves: (
		//	linear: [[0,1/4,2/4,3/4,1],[0,1/4,2/4,3/4,1]],
		//	off: [[0,1/4,2/4,3/4,1],[0,0,0,0,0]],
		//	user: [[0,1/4,2/4,3/4,1],[0,1/4,2/4,3/4,1]]
		//),
		//columns: [
		//	[\target, "Target"],
		//	[\linear, "Linear"],
		//	[\off, "Off"],
		//	[\user, "User"]
		//],
		//rows: [
		//	[\osc1, "Osc 1"],
		//	[\osc2, "Osc 2"],
		//	[\osc3, "Osc 3"],
		//	[\mosc, "M Osc"],
		//	[\insfx, "InsFx"],
		//],
		//val: (
		//	\osc1: \linear,
		//	\osc2: \linear,
		//	\osc3: \linear,
		//	\mosc: \linear,
		//	\insfx: \linear,
		//),
		//destinations: (
		//	\osc1: \osc1_pitch,
		//	\osc2: \osc2_pitch,
		//	\osc3: \osc3_pitch,
		//	\mosc: \modosc_pitch,
		//	//\insfx: \blabla,

		//)
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
		[\curve, \curves, \val].do { arg key;
			self.model[key] = data[key];
		};
	},

	get_current_curve: { arg self;
		self.model.curves[self.model.curve];
	},

	set_curve_value: { arg self, key, curve;
		self.model.curves[key] = curve;
	},

	set_current_curve_value: { arg self, curve;
		self.model.curves[self.model.curve] = curve;
	},

	current_curve_is_editable: { arg self;
		self.model.editable.includes(self.model.curve);
	},

	set_selected_curve_kind: { arg self, kind;
		self.model.curve = kind;
	},

	get_selected_curve_kind: { arg self, kind;
		self.model.curve;
	},

	get_transfert_function: { arg self, kind;
		{ arg val, scale=1;
			var key = self.model.val[kind];
			var env = self.model.curves[key];
			var en;
			en = Env(env[1], env[0][1..].differentiate);
			en.at(val/scale)*scale;
		}
	},

	install_transfert_functions: { arg self;
		//TODO: unused (should set specs)
	},

	set_property: { arg self, name, val, update=true;
		var final_val, mod;
		//[name, val].debug("class_ktrosc_controller: set_property");
		switch(name,
			\value, { 
				self.model.val = val
			},
			\line_value, { 
				self.model.val[val[0]] = val[1]
			},
			\selected_curve_kind, {
				self.set_selected_curve_kind(val);
			}
		);
		if(update) {
			self.changed(\set_property, name, val);
		}
	},

	refresh: { arg self;
		self.changed(\set_property, \value, self.model.val);
		self.changed(\set_property, \selected_curve_kind, self.model.curve);

	},

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
		//paramdata.debug("class_voicing_controller");
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
		// return format is from set_property:\modulation_source
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

	is_muted: { arg self, uname, idx;
		if(self.modulation_dict[[uname,idx]].notNil) {
			self.modulation_dict[[uname, idx]].muted;
		} {
			false;
		}
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
		//[source, val].debug("modulation_manager.set_external_value");
		if(self.modulation_dict[source].isNil) {
			self.modulation_dict[source] = ();
		};
		self.modulation_dict[source].val = val;
		//[self.source_dict].debug("modulation_manager.set_external_value: source_dict");
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
			self[key] = Dictionary.new;
			data[key].keysValuesDo { arg ke, va; 
				self[key][ke] = va;
			};
		};
	},

	get_polarity: { arg self, src;
		var ctrl, ret = \unipolar;
		if(src[0] == \mod) {
			//("modulator%_kind".format(src[1]-3)).debug("get_polarity");
			ctrl = self.main_controller.get_arg("modulator%_kind".format(src[1]-3).asSymbol);
			if(ctrl.notNil) {
				//["ctrl not nil", ctrl.model.val_uname].debug;
				if(ctrl.model.val_uname == \lfo) {
					ret = \bipolar
				}
			}
		  
		};
		ret;
	},

	get_instr_modulation: { arg self;
		var mod = Dictionary.new;
		//self.slot_dict.debug("modulation_manager: get_instr_modulation: slot_dict");
		//self.source_dict.debug("modulation_manager: get_instr_modulation: source_dict");
		//self.modulation_dict.debug("modulation_manager: get_instr_modulation: modulation_dict");
		//self.external_dict.debug("modulation_manager: get_instr_modulation: external_dict");
		self.slot_dict.keysValuesDo { arg dest, source;
			var uname, idx;
			var srckind, srcidx;
			var ret_source, ret_range, ret_norm_range, ret_spec, muted;
			var ret_dest;
			# uname, idx = dest;
			# srckind, srcidx = source;
			if([\mod, \special, \internal].includes(srckind)) {
				//[dest, source].debug("srckindmod");
				ret_source = srcidx;
				if(self.modulation_dict[dest].notNil) {
					var ctrl;
					//[dest, source].debug("modulnotnil");
					ctrl = self.main_controller.get_arg(uname);
					ret_spec = ctrl.model.spec;
					if(self.modulation_dict[dest].muted.not) {
						//[dest, source].debug("modulnotmuted");
						ret_range = self.modulation_dict[dest].range;
						//ret_range.debug("ret_range");
						ret_range = ret_range.clip(ctrl.get_norm_val.neg, 1 - ctrl.get_norm_val);
						//ret_range.debug("ret_range2");
						//[ctrl.get_norm_val, ctrl.model.spec.range, ret_range].debug("normval, specrange, retrange2");
						//ret_range = ctrl.model.spec.map(ret_range.abs) * sign(ret_range);
						ret_norm_range = ret_range;
						ret_range = ctrl.model.spec.range * ret_range;
						//ret_range.debug("ret_range3");
					}
				}
			}; 
			if(ret_source.notNil) {
				if(srckind == \internal) {
					var int_uname;
					//[uname.asString[..8], uname.asString[..8] == "modulator"].debug("modman uname");
					//[uname.asString[..6], uname.asString[..6] == "vibrato"].debug("modman uname");
					case
						{ uname.asString[..8] == "modulator" } {
							ret_dest = uname.asString.drop("modulator1_".size).asSymbol;
							int_uname = (\internal_mod ++ ret_source).asSymbol;
						} 
						{ uname.asString[..6] == "vibrato" } {
							ret_dest = uname.asString.drop("vibrato_".size).asSymbol;
							int_uname = (\internal_mod ++ ret_source).asSymbol;
						}
						{
							int_uname = \error;
						}
					;
					uname = int_uname;
				};
				if(mod[uname].isNil) {
					mod[uname] = Dictionary.new
				};
				mod[uname][idx] = (
					source: ret_source,
					range: ret_range,
					norm_range: ret_norm_range,
					muted: self.modulation_dict[dest] !? _.muted,
				);
				if(srckind == \internal) {
					// dest should be in slot_idx, no ?
					mod[uname][\dest] = ret_dest;
				};
				if(srckind == \special) {
					mod[uname][idx][\special] = ret_source;
					mod[uname][idx][\source] = 0;
				};
				//[uname, ret_spec].debug("get_instr_modulation: set spec");
				mod[uname][\spec] = ret_spec;
			}
		};
		mod;

	},

	get_disabled_modulators: { arg self;
		var ret = (0..7).asSet;
		ret.remove(3); // master amp env
		self.source_dict.keys.do { arg key;
			if(key[0] == \mod) {
				ret.remove(key[1])
			}
		};
		ret
	}
);


~make_midi_note_responder = { arg player;
	var prec;

	if(MIDIClient.initialized.not) {
		MIDIClient.init;
	};
	
	if(player.get_config.notNil) {
		player.get_config.init_jack;
	};

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
				//[src, chan, num, veloc].debug("note on");
				
				if(self.livebook[[chan, num]].isNil) {
					self.livebook[[chan, num]] = livesynth.value(num.midicps, veloc/127);
					self.livebook[[chan, num]].start_time = Process.elapsedTime;

				} {
					num.debug("note node is already playing");
				};


			};
			self.noffr = NoteOffResponder { arg src, chan, num, veloc;
				var note;
				//[self.livebook[[chan,num]], [src, chan, num, veloc]].debug("note off");
				if(self.livebook[[chan,num]].notNil) {
					if((Process.elapsedTime - self.livebook[[chan,num]].start_time)  < 0.02) {
						//(Process.elapsedTime - self.livebook[[chan,num]].start_time ).debug("make_midi_note_responder: noff responder: kill node");
						self.livebook[[chan,num]].synth_node.free;
					};
					self.livebook[[chan,num]].release_node;
					self.livebook[[chan,num]] = nil;
				} {
					"make_midi_note_responder: No note to release".debug;
				}

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

~make_midi_cc_responder = { arg controllers, ccnums, master_ctrl, master_cc;

	var ccresp = List.new;
	var set_if_near;
	var cc_val_list = (-1) ! ccnums.size;
	var master_cc_val = -1;
	CCResponder.removeAll;

	set_if_near = { arg myoldval, val, ctrl;
		var oldval;
		var delta = 0.5/128;
		//var delta = 0.1;
		oldval = ctrl.get_norm_val;
		if(myoldval == oldval or: {
			val.inclusivelyBetween(oldval-delta,oldval+delta)	
		}) {
			{
				ctrl.set_property(\value, val);
			}.defer
		}
	};

	ccnums.do { arg ccnum, idx;
		ccresp.add( CCResponder({ |src,chan,num,value|
				var val = value/127;
				set_if_near.(cc_val_list[idx], val, controllers[idx]);
				cc_val_list[idx] = val;
			},
			nil, // any source
			nil, // any channel
			ccnum, // any CC number
			nil // any value
			)
		)
	};
	if(master_cc.notNil) {
		ccresp.add( CCResponder({ |src,chan,num,value|
				var val = value/127;
				set_if_near.(master_cc_val, val, master_ctrl);
				master_cc_val = val;
			},
			nil, // any source
			nil, // any channel
			master_cc, // any CC number
			nil // any value
			)
		)
	}

};

~class_passive_controller = (
	
	bus_dict: Dictionary.new,
	rebuild_synthdef: true,
	disable_build_synthdef: false,
	synthdef_name: \passive,
	synthdef_fx_name: \passive_fx,
	synthdef_name_suffix: "",

	new: { arg self;
		self = self.deepCopy;
		//"DEBUTTT".debug;
		self.modulation_manager = ~class_modulation_manager.new(self);
		self.fx_feedback_bus = self.get_new_control_bus(\fx_fb);
		//self.fx_feedback_bus.debug("FXFEEDBACK");
		self.fx_bus = self.get_new_audio_bus(\fx, 2);
		self.fx_bypass_bus = self.get_new_audio_bus(\fx_bypass, 2);
		self.make_params;
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
					//uname.debug("build: uname");
					args[\routing][\voicing][uname] = switch(val.model.kind,
						\knob, { val.model.val },
						\static_knob, { val.model.val },
						\spec_knob, { val.model.val },
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
					if(val.model.range.notNil) {
						args[\steps][val.model.indexes[0]][\range] = val.model.range;
					};
					args[\steps][val.model.indexes[0]][val.model.indexes[1]] = val.model.val;
				},
				\mute, {
					args[\enabled][val.model.uname] = (val.model.val == 0);
				}
			);
		};
		//"FIN build args".debug;
		//"build args 1".debug;
		args[\mod] = self.modulation_manager.get_instr_modulation;
		args[\mod][\disabled] = self.modulation_manager.get_disabled_modulators;
		args[\routing][\fx_feedback_bus] = self.fx_feedback_bus.index; // FIXME: must not be shared bus until fx are separated
		//"build args 1".debug;
		args[\routing][\modulation_fxbus] = self.modulation_fxbus.collect(_.index); // FIXME: must not be shared bus until fx are separated

		//synthdef = Instr(\passive).asSynthDef( args );
		//self.synthdef = synthdef;
		//synthdef.add;
		//self.synthdef.debug("synthdef");
		//"build args 1".debug;
		args[\gate] = 1;
		{
			var enabled, kinds, mod, routing, steps;

		//"build args 1".debug;
			enabled = args[\enabled];
			kinds = args[\kinds];
			mod = args[\mod];
			routing = args[\routing];
			steps = args[\steps];
			self.synthdef_ns_args = [enabled, kinds, mod, routing, steps];

		//"build args 1".debug;
			#[ enabled, kinds, mod, routing, steps ].do { arg key; args.removeAt(key) };

			self.synthdef_name = (\passive++self.synthdef_name_suffix).asSymbol;
			self.synthdef_fx_name = (\passive_fx++self.synthdef_name_suffix).asSymbol;

			SynthDef(self.synthdef_name, { 
			//SynthDef(\passive, { 
					var ou;
					ou = SynthDef.wrap(Instr(\passive).func, nil, [enabled, kinds, mod, routing, steps]);
					Out.ar(self.fx_bus.index, ou[0]);
					Out.ar(self.fx_bypass_bus.index, ou[1]);
					//Out.ar(0, ou);
			})
			//.debug("############################synthdef")
			.add;
		//"build args 1".debug;

			SynthDef(self.synthdef_fx_name, { arg out = 0;
			//SynthDef(\passive_fx, { arg out = 0;
					var ou;
					var in = In.ar(self.fx_bus.index, 2);
					var in_bypass = In.ar(self.fx_bypass_bus.index, 2);
					ou = SynthDef.wrap(Instr(\passive_fx).func, nil, [in, in_bypass, enabled, kinds, mod, routing, steps]);
					ou = Out.ar(out, ou);
			}).add;
		//"build args 1".debug;
		}.value;
		self.synthdef_args = args;
		//"build args 1".debug;
		//self.patch = Patch(\passive,  self.synthdef_args );
		//self.patch.invalidateSynthDef;
		//self.rebuild = false;
		args.postcs;
	},

	update_arg: { arg self, uname;
		// TODO
		if(self.disable_build_synthdef) {
			//uname.debug("class_passive_controller: disabled update_arg");
		} {
			//uname.debug("class_passive_controller: update_arg");
			self.build_synthdef;
			self.rebuild_synthdef = true;
		};
	},

	compute_freq: { arg self, freq, destdict;
		var dict = Dictionary.new;
		var ktrosc = self.get_arg(\ktrcurve_osc);
		var ktrfilt = self.get_arg(\ktrcurve_filter);
		var midi = freq.cpsmidi;
		//midi.debug("compute_freq: midi");

		dict[\freq] = freq;
		dict[\ktr_osc1_freq] = ktrosc.get_transfert_function(\osc1).(midi, 128).midicps;
		dict[\ktr_osc2_freq] = ktrosc.get_transfert_function(\osc2).(midi, 128).midicps;
		dict[\ktr_osc3_freq] = ktrosc.get_transfert_function(\osc3).(midi, 128).midicps;
		dict[\ktr_mosc_freq] = ktrosc.get_transfert_function(\mosc).(midi, 128).midicps;
		dict[\ktr_insfx_freq] = ktrosc.get_transfert_function(\insfx).(midi, 128).midicps;

		dict[\ktr_filter1_freq] = ktrfilt.get_transfert_function(\filter1).(midi, 128).midicps;
		dict[\ktr_filter2_freq] = ktrfilt.get_transfert_function(\filter2).(midi, 128).midicps;

		//dict.debug("compute_freq");

		if(destdict.notNil) {
			destdict.putAll(dict);
			destdict;
		} {
			dict;
		};
	},

	get_piano: { arg self;
		{ arg freq=400, velocity=0.5;
			var synth;
			var busses;
			//"###########----- making note".debug;
			self.compute_freq(freq, self.synthdef_args);
			self.synthdef_args[\velocity] = velocity;
			//p = Patch(\passive,  self.synthdef_args );
			s.makeBundle(nil, {
				busses = 8.collect { arg idx;
					Bus.audio(s, 1);
				};
				self.synthdef_args[\modulation_bus] = busses;
				if(self.note_group.isNil or: {self.note_group.isPlaying.not}) {
					//"########### making group".debug;
					self.note_group = Group.new(s);
					self.note_group.register;
				};

				//"########### making synth".debug;
				synth = Synth(self.synthdef_name, self.synthdef_args.getPairs, self.note_group);
				//synth = Synth(\passive, self.synthdef_args.getPairs, self.note_group);

				if(self.fx_node.isNil or: {self.fx_node.isPlaying.not or:{self.rebuild_synthdef}}) {
					//"########### making fx".debug;
					self.fx_node.release;
					self.fx_node = Synth(self.synthdef_fx_name, self.synthdef_args.getPairs, self.note_group, \addAfter);
					//self.fx_node = Synth(\passive_fx, self.synthdef_args.getPairs, self.note_group, \addAfter);
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
					//"********************* releasing synth".debug;
					synth.release;
					self.busses.do { arg bus; bus.free; }
				};
			
			);
		}
	
	},

	make_midi_responder: { arg self;
		var ccnums, master_cc, cc_ctrls;

		ccnums = self.get_config.macro_midi_cc;
		cc_ctrls = 8.collect { arg x; self.get_arg("macro%_control".format(x+1).asSymbol) };
		self.cc_responder = ~make_midi_cc_responder.(cc_ctrls, ccnums, self.get_arg(\amp), self.get_config.master_volume_cc);

		self.midi_note_responder = ~make_midi_note_responder.(self);
		self.midi_note_responder.start_liveplay;
		self.midi_note_responder;
	},

	get_config: { arg self;
		~passive_config;
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
			//key.debug("saving key");
			ret[key] = self.data[key].save_data;
		};
		preset[\data] = ret;
		preset;
	},

	load_preset: { arg self, preset;
		//preset.name.debug("=================loading preset");
		self.modulation_manager.load_data(preset[\modulation_manager]);
		self.disable_build_synthdef = true;
		preset.data.keys.do { arg key;
			//key.debug("loading key");
			self.data[key].load_data(preset.data[key]);
			self.data[key].refresh;
		};
		self.disable_build_synthdef = false;
		self.rebuild_synthdef = true;
		self.build_synthdef;
	},

	load_preset_by_uname: { arg self, uname;
		self.get_arg(\presets_global).load_preset_by_uname(uname);
	},

	save_current_preset_as_uname: { arg self, uname;
		self.get_arg(\presets_global).save_current_preset_as_uname(uname);
	},

	make_gui: { arg self;
		self.view = ~class_passive_view.new(self);
	}
);

