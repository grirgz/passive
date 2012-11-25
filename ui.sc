
~make_class_responder = { arg self, parent, model, list, auto_refresh=true;
	var controller;

	controller = SimpleController(model);
	list.do { arg method;
		controller.put(method, { arg ... args; self[method].(self, *args) });
	};

	parent.onClose = parent.onClose.addFunc { controller.remove };

	if(auto_refresh) { 
		if(model.notNil) {
			model.refresh()
		} {
			"make_class_responder: Model is nil".debug;
		}
	};

	controller;
};

~doubleclip = { arg val, mi, ma;
	if(val < mi) {
		val = mi;
	} {
		if(val > ma) {
			val = ma;
		}
	};
	val;
};

~keycodes = (
	enter:10,
	escape: 27
);


////////////////////// GUIs

~class_load_wavetable_dialog = (
	apply_done: false,
	new: { arg self, apply_action, cancel_action, path;
		var wtpath, folderlist, filelist, filepath, selectedlist = List.new, selectedfile;
		self = self.deepCopy;

		self.buffer = Buffer.alloc(s, 4096);

		wtpath = path ?? "~/Musique/archwavetable/Architecture Waveforms 2010 Wav24/Architecture Waveforms 2010 Wav24".standardizePath;
		self.signal = Signal.newClear(2048);
		folderlist = PathName.new(wtpath).folders;

		self.window = Window.new("choose wavetable", Rect(0,0,1300,400));
		self.window.onClose = {
			self.buffer.free;
			self.synthnode.release;
			if(self.apply_done.not) {
				cancel_action.();
			}
		};
		self.layout = HLayoutView.new(self.window, Rect(0,0,1300,400));

		self.folderlistview = ListView.new(self.layout, Rect(0,0,300,400));
		self.folderlistview.items = folderlist.collect{ arg folder; folder.folderName };
		self.folderlistview.action = { arg view, b, c;
			[a, b, c].debug("action");
			filelist = folderlist[view.value].files;
			self.filelistview.items = filelist.collect { arg file; file.fileName };
			self.filelistview.value = 0;
			self.filelistview.action.value(self.filelistview);
		};

		self.filelistview = ListView.new(self.layout, Rect(0,0,300,400));
		//self.filelistview.items = ["bla", "rah"];
		self.filelistview.action = { arg view, b, c;
			[view, b, c].debug("filelist action");
			selectedfile = filelist[view.value];
			self.display_file(selectedfile);
		};

		self.right_layout = VLayoutView.new(self.layout, Rect(0,0,300,400));

		self.buttons_layout = HLayoutView.new(self.right_layout, Rect(0,0,300,20));

		self.but_play = Button.new(self.buttons_layout, Rect(0,0,50,20));
		self.but_play.string = "Play";
		self.but_play.action = {
			var path;
			path = self.displayed_file.fullPath;
			path.debug("play: path");
			self.synthnode.debug("synth");
			if(self.synthnode.isNil.debug("isNil")) {
				"ou".debug;
				~load_sample_in_wavetable_buffer.(self.buffer, path);
				self.synthnode = { Osc.ar(self.buffer, MouseX.kr(20, 380), mul:0.1) ! 2  }.play;
				self.but_play.string = "Stop";
			} {
				"iou".debug;
				self.synthnode.release;
				self.synthnode = nil;
				self.but_play.string = "Play";
			}
		};

		self.but_add = Button.new(self.buttons_layout, Rect(0,0,50,20));
		self.but_add.string = "+ Add";
		self.but_add.action = {
			selectedlist.add(selectedfile);
			self.selectedlistview.items = selectedlist.collect(_.fileName);
		};

		self.but_add = Button.new(self.buttons_layout, Rect(0,0,50,20));
		self.but_add.string = "- Rem";
		self.but_add.action = {
			selectedlist.removeAt(self.selectedlistview.value);
			self.selectedlistview.items = selectedlist.collect(_.fileName);
		};

		StaticText.new(self.buttons_layout, Rect(0,0,10,20)); //spacer

		self.but_movedown = Button.new(self.buttons_layout, Rect(0,0,50,20));
		self.but_movedown.string = "Down";
		self.but_movedown.action = {
			var pos, newpos, item;
			pos = self.selectedlistview.value;
			item = selectedlist.removeAt(pos);
			selectedlist.insert((pos+1).clip(0,selectedlist.size), item);
			self.selectedlistview.value = (pos+1).clip(0,selectedlist.size-1);
			self.selectedlistview.items = selectedlist.collect(_.fileName);
		};

		self.but_moveup = Button.new(self.buttons_layout, Rect(0,0,50,20));
		self.but_moveup.string = "Up";
		self.but_moveup.action = {
			var pos, newpos, item;
			pos = self.selectedlistview.value;
			item = selectedlist.removeAt(pos);
			selectedlist.insert((pos-1).clip(0,selectedlist.size), item);
			self.selectedlistview.value = (pos-1).clip(0,selectedlist.size-1);
			self.selectedlistview.items = selectedlist.collect(_.fileName);
		};

		self.selectedlistview = ListView.new(self.right_layout, Rect(0,0,300,150));
		self.selectedlistview.items = selectedlist.collect(_.fileName);
		self.selectedlistview.action = { arg view, b, c;
			[a, b, c].debug("action");
			self.display_file(selectedlist[self.selectedlistview.value]);
		};
		
		self.plotter = Plotter("plot", parent: self.right_layout);

		self.but_apply = Button.new(self.right_layout, Rect(0,0,80,20));
		self.but_apply.string = "Apply";
		self.but_apply.action = {
			apply_action.(selectedlist);
			self.apply_done = true;
			self.window.close;
		};

		//self.pack_layout = VLayoutView.new(self.layout, Rect(0,0,300,400));


		//self.pack_title = TextField.new(self.pack_layout, Rect(0,0,300,20));

		//self.packlistview = ListView.new(self.pack_layout, Rect(0,0,300,360));
		//self.packlistview.items = selectedlist.collect(_.fileName);
		//self.packlistview.action = { arg view, b, c;
		//	[a, b, c].debug("action");
		//	self.display_file(selectedlist[self.selectedlistview.value]);
		//};


		self.folderlistview.value = 0;
		self.folderlistview.action.value(self.folderlistview);

		self.window.front;

		self;
	},

	display_file: { arg self, file;
		var sf, sig;
		self.displayed_file = file;
		sf = SoundFile.openRead(file.fullPath);
		sf.readData(self.signal);
		sf.close;
		self.plotter.value = self.signal.as(Array);
		self.window.refresh;
		if(self.synthnode.notNil) {
			~load_signal_in_wavetable_buffer.(self.buffer, self.signal);
		};
	}

);

~class_save_preset_dialog = (
	apply_done: false,
	new: { arg self, list, apply_action, cancel_action;
		self = self.deepCopy;

		self.window = Window.new("save preset as", Rect(0,0,300,400));
		self.window.onClose = {
			if(self.apply_done.not) {
				cancel_action.();
			}
		};
		self.layout = VLayoutView.new(self.window, Rect(0,0,300,400));

		self.window.view.keyDownAction = { arg view, char, modifiers, unicode, keycode;
			"class_save_preset_dialog: keyDownAction".debug;
			if(keycode == ~keycodes.escape) {
				self.window.close;
			};
			if(keycode == ~keycodes.enter) {
				self.but_apply.action.value;
			};
			view.defaultKeyDownAction(char, modifiers, unicode, keycode);

		};

		self.preset_name_field = TextField.new(self.layout, Rect(0,0,300,30));
		self.preset_name_field.keyDownAction = self.window.view.keyDownAction;

		self.presetlistview = ListView.new(self.layout, Rect(0,0,300,300));
		self.presetlistview.items = list;
		self.presetlistview.action = { arg view, b, c;
			self.preset_name_field.string = list[view.value];
		};

		self.but_apply = Button.new(self.layout, Rect(0,0,80,20));
		self.but_apply.string = "Apply";
		self.but_apply.action = {
			if(self.preset_name_field.string != "") {
				apply_action.(self.preset_name_field.string);
				self.apply_done = true;
			};
			self.window.close;
		};

		self.window.front;

		self;
	}
);


////////// LFO gui elements

~class_curvegraph_view = (
	draw_columns: false,
	change_curve_mode: false,
	current_curve_pen: \sin1,

	new: { arg self, parent, size, controller, curve_edit;
		self = self.deepCopy;
		size = size ?? (300@300);
		self.controller = controller;
		self.curve_edit = curve_edit ?? (change_curve_mode:false);
		self.numstep = controller.get_numstep ?? 16;
		self.ampmods = 1 ! self.numstep;
		self.curves = [\sin4, \sin2, \line1, \negline1, \sin1];
		self.draw_columns = true;
		self.graph_size = size;
		self.curvebank = controller.get_curvebank ?? ~curvebank;
		self.curvegraph = self.make_curvegraph(parent, size);
		self.phase = 0;

		~make_class_responder.(self, self.curvegraph, self.controller, [ \set_property ]);
		self;
	},

	set_property: { arg self, controller, msg, name, val;
		[name, val].debug("class_curvegraph_view: set_property");
		switch(name,
			\curve, { 
				self.curves = val;
				self.curvegraph.refresh;
			},
			\curve_amps, {
				self.ampmods = val;
				self.curvegraph.refresh;
			}
		)
	},

	make_curvegraph: { arg self, parent, size;
		var userview;

		userview=UserView(parent, Rect(0,0,size.x,size.y));
		userview.background_(Color.black);
		userview.mouseMoveAction = { arg view, x, y;
			var nx, ny, ox, oy;
			ny = 1-(y/size.y);
			nx = (x/size.x).trunc(1 / self.numstep)* self.numstep;
			ox = nx.clip( 0, self.numstep );
			oy = ny.clip( 0, 0.999 );
			[x,y, nx, ny, ox, oy].debug("x,y, nx, ny, ox, oy");
			
			if(self.curve_edit.change_curve_mode) {
				self.curves.wrapPut(ox, self.curve_edit.curve_shape);
				self.controller.set_property(\curve, self.curves, false);

			} {
				self.ampmods[ox] = oy;
				self.controller.set_property(\curve_amps, self.ampmods, false);
			};
			view.refresh;
		};

		userview.mouseDownAction = userview.mouseMoveAction;

		userview.drawFunc={|uview|
			var draw_band;
			var numstep = self.numstep;
			var ampmods = self.ampmods;
			//"--------------begin draw".debug;

			Pen.color = Color.white;
			Pen.width = 2;
			draw_band = { arg offset, scale, ampmod, phase, fun;
				var x, y;
				var scalefun;
				scalefun = { arg x, y; 
					y = fun.(x/scale.x+phase)*ampmod;
					y = (1-y)*scale.y;
					y = y.trunc;
					y;
				};
				x = 0;
				y = scalefun.(x, y);
				Pen.lineTo(offset@y);
				scale.x.do { arg x;
					x = x+1;
					y = scalefun.(x, y);
					//x = x*scale.x;
					x = x+offset;
					//[x, y].debug("scaledplop");
					Pen.lineTo(x@y);
				};

			};
			Pen.moveTo(0@0);

			numstep.do { arg offset, i;
				//"-------begin band".debug;
				draw_band.(
					offset*(size.x/numstep).trunc, 
					(size.x/numstep).trunc @ size.y,
					ampmods.wrapAt(i),
					self.phase,
					self.curvebank[self.curves.wrapAt(i)]
				);
			};
			Pen.stroke;


			if(self.draw_columns) {
				Pen.color = Color.grey(0.5);
				Pen.width = 1;
				numstep.do { arg offset;
					Pen.lineDash = [0.5,1]*5;
					//"-------begin sep".debug;
					offset = (offset+1)*(size.x/numstep);
					Pen.line(offset@0, offset@size.y);
				};
				Pen.stroke;
			}
			


		};
		userview;
	}
);

~class_mini_curvegraph_view = (
	parent: ~class_curvegraph_view,

	new: { arg self, parent, size, curvebank, curve, curve_edit;
		self = self.deepCopy;
		size = size ?? (300@300);
		self.numstep = 1;
		self.controller = ();
		self.ampmods = 1 ! self.numstep;
		self.curves = [curve];
		self.phase = 0;
		self.graph_size = size;
		self.curvebank = curvebank;
		self.curvebank.debug("class_simple_curvegraph_view: curvebank");
		self.curvegraph = self.make_curvegraph(parent, size);

		self.curvegraph.mouseDownAction =  { arg view, x, y;
			curve_edit.curve_shape = curve;
			curve_edit.change_curve_mode = true;
			curve_edit.debug("class_mini_curvegraph_view: mousedown");
		};
		self.curvegraph.mouseMoveAction =  { arg view, x, y;
			// noop
		};

		self;
	}

);

~class_simple_curvegraph_view = (
	parent: ~class_curvegraph_view,

	new: { arg self, parent, size, ctrl; //,curvebank, curve;
		self = self.deepCopy;
		size = size ?? (300@300);
		self.numstep = 1;
		self.controller = ctrl;
		self.ampmods = 1 ! self.numstep;
		self.curves = [\sin1];
		self.phase = 0;
		self.graph_size = size;
		self.curvebank = ctrl.get_curvebank ?? ~curvebank;
		self.curvebank.debug("class_simple_curvegraph_view: curvebank");
		self.curvegraph = self.make_curvegraph(parent, size);

		self.curvegraph.mouseDownAction =  { arg view, x, y;
			self.x_offset = x;
			self.value_offset = self.phase;
			"down".postln;
		};

		self.curvegraph.mouseMoveAction =  { arg view, x, y;
			var ro, nx;
			//[x, x - ~x, ~x, ro].debug("x, x-~x, ~x");
			nx = x - self.x_offset;
			"before doubleclip".debug;
			ro = ((nx/100) + self.value_offset).clip( 0, 0.999 );
			"after doubleclip".debug;
			
			self.phase = 1-ro;
			self.curvegraph.refresh;
		};
		~make_class_responder.(self, self.curvegraph, self.controller, [ \set_property ]);

		self;
	},

	set_property: { arg self, controller, msg, name, val;
		[name, val].debug("class_simple_curvegraph_view: set_property");
		switch(name,
			\curve, { 
				self.curves = [val];
				self.curvegraph.refresh;
			}
		)
	},

);

~class_curve_select_view = (
	new: { arg self, parent, size, controller;
		var buttonfun;
		var select_action;
		self = self.deepCopy;
		self.controller = controller;
		self.layout = VLayoutView.new(parent, Rect(0,0,size.x,size.y));

		select_action = { arg popup;
			var curve = controller.get_menu_items_names[popup.value];
			self.controller.set_property(\curve, curve, true);
		};
		~class_popup_view.new(self.layout, 80@20, controller, select_action);

		self.buttons_layout = HLayoutView.new(self.layout, Rect(0,0,size.x,30));
		self.old_idx = nil;
		buttonfun = { arg name, idx;
			var but;
			but = Button.new(self.buttons_layout, 50@20);
			but.states = [
				[name, Color.black, Color.clear],
				[name, Color.black, Color.gray(0.5)],
			];
			but.action = { arg button;
				//self.select_button(idx);
				self.controller.set_property(\curve, self.buttons_uname[idx], true);

			};
			but;
		};
		self.buttons_uname = [\sin1, \saw1, \square1, \triangle1];
		self.buttons = ["Sin", "Saw", "Square", "Triangle"].collect { arg name, idx;
			buttonfun.(name, idx);
		};
		~make_class_responder.(self, self.layout, self.controller, [ \set_property ]);

		self;
	},

	select_button: { arg self, idx;
		if(self.old_idx.notNil) {
			self.buttons[self.old_idx].value = 0;
		};
		if(idx.notNil) {
			self.buttons[idx].value = 1;
			self.old_idx = idx;
		}
	},

	set_property: { arg self, controller, msg, name, val;
		var idx;
		[name, val].debug("class_curve_select_view: set_property");
		switch(name,
			\curve, { 
				idx = self.buttons_uname.indexOf(val);
				self.select_button(idx);
			}
		)
	}

);

~class_env_view = (
	new: { arg self, parent, size, controller;
		self = self.deepCopy;
		self.view_size = size;

		self.make_env(parent, size);
		

		self;
	}, 

	make_env: { arg self, parent, size, val;
		val = val ?? [[0.0, 0.1, 0.5, 1.0],[0.1,1.0,0.8,0.0]];
		size = size ?? (50@50);

		self.env_view = EnvelopeView(parent, size.asRect)
		    .drawLines_(true)
		    .selectionColor_(Color.red)
		    .drawRects_(true)
		//    .resize_(5)
		    .step_(0.05)
		    .action_({arg b; [b.index, b.value].postln})
		    .thumbSize_(5)
		    .value_(val);

	}

);

~class_ar_env_view = (
	parent: ~class_env_view,
	new: { arg self, parent, size, at_controller, rt_controller;
		self = self.deepCopy;
		self.at_controller = at_controller;
		self.rt_controller = rt_controller;

		self.attack_time = 0.5;
		self.release_time = 0.5;

		self.make_env( parent, size);
		self.env_view.setEnv( Env.perc(self.attack_time, self.release_time, 1, 0) );

		~make_class_responder.(self, self.env_view, at_controller, [ \set_property ]);
		~make_class_responder.(self, self.env_view, rt_controller, [ \set_property ]);
		
		self;
	},

	set_property: { arg self, controller, msg, name, val;
		[name, val].debug("class_ar_env_view: set_property");
		switch(name,
			\value, { 
				if(controller === self.at_controller) {
					self.attack_time = controller.get_val;
				} {
					self.release_time = controller.get_val;
				};
				self.env_view.setEnv( Env.perc(self.attack_time, self.release_time, 1, 0) );
			}
		)
	}

);

~class_dadsr_env_view = (
	parent: ~class_env_view,
	new: { arg self, parent, size, idx, main_controller;
		var makectrl;
		self = self.deepCopy;
		idx.debug("new class_dadsr_env_view");

		self.make_env( parent, size);
		self.responder = Dictionary.new;
		self.val_dict = Dictionary.new;

		makectrl = { arg name;
			var ctrl;
			ctrl = main_controller.get_env_arg(idx, name);
			self.responder[name] = {
				self.val_dict[name] = ctrl.get_val;
			};
			[name, ctrl.get_val].debug("class_dadsr_env_view: makectrl: get_val");
			self.val_dict[name] = ctrl.get_val;
			~make_class_responder.(self, self.env_view, ctrl, [ \set_property ], false);
		};

		[\delay_time, \attack_time, \attack_level, \decay_time, \decay_level, \release_time].do { arg name;
			makectrl.(name);
		};

		idx.debug("end new class_dadsr_env_view");

		self;
	},

	set_property: { arg self, controller, msg, name, val;
		var uname_key;
		[name, val].debug("class_dadsr_env_view: set_property");
		switch(name,
			\value, { 
				uname_key = controller.model.uname.asString.drop("env1_".size).asSymbol;
				self.responder[uname_key].value;
				[controller.model.uname, uname_key, self.val_dict].debug("class_dadsr_env_view: set_property: val_dict");
				self.env_view.setEnv( 
					Env.dadsr(
						self.val_dict[\delay_time],
						self.val_dict[\attack_time],
						self.val_dict[\decay_time],
						self.val_dict[\decay_level]/self.val_dict[\attack_level],
						self.val_dict[\release_time],
						self.val_dict[\attack_level],
						0
					) 
				);
			}
		);
		[name, val].debug("end class_dadsr_env_view: set_property");
	}

);


~class_rateamp_frame = (
	new: { arg self, parent, size, rate_controller, amp_controller;
		self = self.deepCopy;
		self.view_size = size ?? (112@100);
		self.layout = VLayoutView.new(parent, Rect(0,0,self.view_size.x,self.view_size.y));
		self.rate_knob = ~class_pknob_view.new(self.layout, self.view_size.x@(self.view_size.y/2), rate_controller);
		self.amp_knob = ~class_pknob_view.new(self.layout, self.view_size.x@(self.view_size.y/2), amp_controller);
		self;
	}
);

~class_lfo_frame = (
	new: { arg self, parent, frame_size, index, main_controller;
		var controller = nil;
		var block_size;
		var ctrl = { arg name; main_controller.get_mod_arg(index, name) };
		self = self.deepCopy;
		
		frame_size = frame_size ?? (812@300);
		//frame_size = (812@100);
		block_size = 140@((frame_size.y/2)-5);
		self.layout = HLayoutView.new(parent, Rect(0,0,frame_size.x,frame_size.y));
		"prapri1".debug;

		self.rateamp = ~class_rateamp_frame.new(self.layout, (100@frame_size.y), ctrl.(\rate), ctrl.(\amp));
		self.slider1 = ~class_pslider_view.new(self.layout, nil, ctrl.(\glidefade));

		"prapri2".debug;
		self.curve_layout = VLayoutView.new(self.layout, Rect(0,0,block_size.x,frame_size.y));
		[index, ctrl.(\curve1)].debug("class_lfo_frame: ctrl curve1");
		self.curve1 = ~class_simple_curvegraph_view.new(self.curve_layout, block_size, ctrl.(\curve1));
		self.curve2 = ~class_simple_curvegraph_view.new(self.curve_layout, block_size, ctrl.(\curve2));

		"prapri3".debug;
		self.curve_select_layout = VLayoutView.new(self.layout, Rect(0,0,block_size.x*2,frame_size.y));

		self.curve_select1 = ~class_curve_select_view.new(self.curve_select_layout, block_size, ctrl.(\curve1));
		self.curve_select2 = ~class_curve_select_view.new(self.curve_select_layout, block_size, ctrl.(\curve2));
		"prapri4".debug;

		self.env_layout = VLayoutView.new(self.layout, Rect(0,0,block_size.x,block_size.y));
		self.env_view = ~class_ar_env_view.new(self.env_layout, block_size, ctrl.(\env_attack), ctrl.(\env_decay));
		self.env_control_layout = HLayoutView.new(self.env_layout, block_size.asRect);
		"prapri5".debug;
		self.knob1 = ~class_pknob_view.new(self.env_control_layout, nil, ctrl.(\env_attack));
		self.knob1 = ~class_pknob_view.new(self.env_control_layout, nil, ctrl.(\env_decay));

		self;
	}
);

~class_stepper_view = (
	new: { arg self, parent, size, index, main_controller;
		var ctrl = { arg name; main_controller.get_mod_arg(index, name) };
		self = self.deepCopy;
		self.main_controller = { arg self; main_controller };
		self.ctrl = { arg self, name; ctrl.(name) };
		self.view_size = size ?? (512@300);
		self.multislider_size = self.view_size.x @ (self.view_size.y - 70);
		self.numstep = ctrl.(\steps1).get_numstep ?? 16;
		self.stepper_layout = VLayoutView.new(parent, Rect(0,0,self.view_size.x,self.view_size.y));
		self.rangeslider = self.make_rangeslider(self.stepper_layout);
		self.numheader = self.make_numheader(self.stepper_layout);

		self.multislider = self.make_multislider(self.stepper_layout, ctrl.(\steps_amp));
		self.multislider.action = { arg ms;
			"action bordel!!!!".debug;
			ctrl.(\steps_amp).set_property(\value, ms.value, \false);
		};

		self.glidegrid = self.make_boxgrid(self.stepper_layout, ctrl.(\steps1));
		self.ampgrid = self.make_boxgrid(self.stepper_layout, ctrl.(\steps2));

		~make_class_responder.(self, self.stepper_layout, ctrl.(\steps_amp), [ \set_property ]);
		~make_class_responder.(self, self.stepper_layout, ctrl.(\steps1), [ \set_property ]);
		~make_class_responder.(self, self.stepper_layout, ctrl.(\steps2), [ \set_property ]);

		self;
	},

	make_multislider: { arg self, parent, controller;
		var multislider;
		var size;
		size = self.numstep ?? 16;

		multislider = MultiSliderView(parent, Rect(0, 00, self.multislider_size.x, self.multislider_size.y));   
		controller.get_val.debug("class_stepper_view: controller.get_val");
		multislider.value_(controller.get_val);
		multislider.isFilled_(true); // width in pixels of each stick
		multislider.drawRects = true;
		multislider.indexThumbSize_(self.view_size.x/self.numstep); // spacing on the value axis
		multislider.valueThumbSize_(1); // spacing on the value axis
		multislider.showIndex = true;
		multislider.step = 0.1;
		multislider.fillColor = Color.gray(0.5);
		//multislider.resize = 5;
		multislider.gap_(0);

		multislider;
	},

	make_rangeslider: { arg self, parent, size;
		var rangeslider;
		size = size ?? (self.view_size.x@10);

		rangeslider = RangeSlider(parent, Rect(0, 0, size.x, size.y))
		    .lo_(0.0)
		    .hi_(0.5);
		rangeslider.step = 1/self.numstep;
		//rangeslider.step = 0.1;
		rangeslider;

	},

	make_numheader: { arg self, parent, size;
		var layout;
		var text;
		size = size ?? (self.view_size.x@15);
		layout = HLayoutView.new(parent, Rect(0,0,size.x,size.y));
		self.numstep.do { arg idx;
			idx = idx+1;
			text = StaticText.new(layout, Rect(0,0,size.x/self.numstep-4,size.y));
			text.string = "  " ++ idx.asString;
			text.background = Color.gray(0.5);
		};

	},

	make_boxgrid: { arg self, parent, controller, size;
		var boxgrid;
		size = size ?? (self.view_size.x@10);
		boxgrid = BoxGrid.new(parent, bounds: Rect(0,0,size.x, size.y), columns: 16, rows: 1);

		boxgrid.setTrailDrag_(true, true);
		boxgrid.setNodeBorder_(2);
		boxgrid.nodeDownAction = { arg nodeloc;
			controller.set_property(\value, boxgrid.getNodeStates[0].debug("class_stepper_view: make_boxgrid: node states"), false);
		};
		boxgrid;
	},

	//////////

	set_property: { arg self, controller, msg, name, val;
		[controller.model.uname, self.ctrl(\steps1), name, val].debug("class_stepper_view: set_property");
		switch(name,
			\value, { 
				if(controller == self.ctrl(\steps_amp) ) {
					"setting steps_amp".debug;
					self.multislider.value = val;
				};
				if(controller == self.ctrl(\steps1) ) {
					"setting steps1".debug;
					self.glidegrid.setNodeStates = [val];
				};
				if(controller == self.ctrl(\steps2) ) {
					"setting steps2".debug;
					self.ampgrid.setNodeStates = [val];
				};

			}
		)
	}
);

~class_performer_view = (
	parent: ~class_stepper_view,

	new: { arg self, parent, size, index, main_controller, curve_edit;
		var ctrl = { arg name; main_controller.get_mod_arg(index, name) };
		self = self.deepCopy;
		self.ctrl = { arg self, name; ctrl.(name) };
		self.view_size = size ?? (512@300);
		self.numstep = ctrl.(\steps1).get_numstep ?? 16;
		self.stepper_layout = VLayoutView.new(parent, Rect(0,0,self.view_size.x,self.view_size.y));
		self.rangeslider = self.make_rangeslider(self.stepper_layout);
		self.numheader = self.make_numheader(self.stepper_layout);
		self.curvegraph1 = ~class_curvegraph_view.new(self.stepper_layout, self.view_size.x@80, ctrl.(\perfcurve1), curve_edit);
		self.curvegraph2 = ~class_curvegraph_view.new(self.stepper_layout, self.view_size.x@80, ctrl.(\perfcurve2), curve_edit);
		self.glidegrid = self.make_boxgrid(self.stepper_layout, ctrl.(\steps1));
		self.ampgrid = self.make_boxgrid(self.stepper_layout, ctrl.(\steps2));

		~make_class_responder.(self, self.stepper_layout, ctrl.(\steps1), [ \set_property ]);
		~make_class_responder.(self, self.stepper_layout, ctrl.(\steps2), [ \set_property ]);

		self;
	}
);

~class_env_edit_view = (

	new: { arg self, parent, size, index, controller;
		self = self.deepCopy;
		
		size = size ?? (752@400);
		self.layout = VLayoutView.new(parent, size.asRect);
		self.env_layout = HLayoutView.new(self.layout, Rect(0,0,size.x,(size.y/2)-30));
		self.velocity = ~class_pslider_view.new(self.env_layout, 50@self.env_layout.bounds.height, controller.get_env_arg(index, \vel));
		self.keytrack = ~class_pslider_view.new(self.env_layout, 50@self.env_layout.bounds.height, controller.get_env_arg(index, \ktr));
		self.env_view = ~class_dadsr_env_view.new(self.env_layout, (size.x-360)@self.env_layout.bounds.height, index, controller);

		self.env_control_layout = HLayoutView.new(self.layout, Rect(0,0,size.x,(size.y/2)+30));

		[\delay_time, \attack_time, \attack_level, \decay_time, \decay_level, \sustain_time, \sustain_level, \release_time].do { arg name;
			[name, index].debug("class_env_edit_view: making env knob");
			controller.get_env_arg(index, name).debug("class_env_edit_view: ctrl");
			~class_pknob_view.new(self.env_control_layout, nil, controller.get_env_arg(index, name));
		};

		self;
	}

);


~class_perfstep_frame = (
	new: { arg self, parent, frame_size, index, main_controller, kind;
		var ctrl = { arg name; main_controller.get_mod_arg(index, name) };
		self = self.deepCopy;

		self.curve_edit = (
			change_curve_mode: false,
			curve_shape: \sin1
		);
		
		self.kind = kind;
		self.ctrl = { arg self, name; ctrl.(name) };
		frame_size = frame_size ?? ((512+300)@300);
		self.frame_size = frame_size;
		self.layout = HLayoutView.new(parent, Rect(0,0,frame_size.x,frame_size.y));

		self.outer_right_layout = HLayoutView.new(self.layout, Rect(0,0,230, frame_size.y));
		self.set_right_panel(\normal, true);

		self.numstep = 16;
		
		if(kind == \performer) {
			self.graphview = ~class_performer_view.new(self.layout, (512)@frame_size.y, index, main_controller, self.curve_edit);
		} {
			self.graphview = ~class_stepper_view.new(self.layout, (512)@frame_size.y, index, main_controller);
		};

		self;
	},

	set_right_panel: { arg self, kind, force=false;
		var curvebank = ~curvebank; // TODO: use ctrl.get_curvebank
		var curvelist = curvebank.keys.asList;
	
		if(self.kind == \performer or:{force}) {

			if(self.outer_right_layout.children.notNil) {
				self.outer_right_layout.children[0].remove;
			};
			self.right_layout = HLayoutView.new(self.outer_right_layout, Rect(0,0,230, self.frame_size.y));
			[kind, self.kind].debug("class_perfstep_frame: set_right_panel");
			if(kind == \curves) {
				self.curve_select = ~class_load_curve_frame.new(
					self.right_layout, Point(230,self.frame_size.y), curvebank, curvelist, self.curve_edit
				);
			} {
				self.curve_edit.change_curve_mode = false;
				self.rateamp = ~class_rateamp_frame.new(self.right_layout, nil, self.ctrl(\rate), self.ctrl(\amp));
				self.slider1 = ~class_pslider_view.new(self.right_layout, 50@(self.frame_size.y-70), self.ctrl(\ampmod));
				self.slider2 = ~class_pslider_view.new(self.right_layout, 50@(self.frame_size.y-70), self.ctrl(\glidefade));
			}
		}
	}
);

~class_lfoperfstep_frame = (
	new: { arg self, parent, frame_size, idx, main_controller;
		var inner_frame_size;
		self = self.deepCopy;
		self.main_controller = { arg self; main_controller };
		frame_size = frame_size ?? (812@300);
		self.layout = VLayoutView.new(parent, frame_size.asRect);
		self.mod_index = idx;
		
		"nain1".debug;
		self.header_layout = HLayoutView.new(self.layout, frame_size.x@20);

		self.load_button = Button.new(self.header_layout, 80@30);
		self.load_button.states = [
			["Load curve"],
			["Normal mode"]
		];
		self.load_button.action = {
			self.body.set_right_panel([\normal, \curves][self.load_button.value])
		};

		self.save_button = Button.new(self.header_layout, 80@30);
		self.save_button.states = [["Save"]];

		self.delete_button = Button.new(self.header_layout, 80@30);
		self.delete_button.states = [["Delete"]];

		"nain2".debug;

		self.select_popup = ~class_popup_view.new(self.header_layout, nil, main_controller.get_mod_arg(idx, \kind), { arg popup;
			popup.value.debug("action!!!!");
			self.update_body;
		});
		"nain3".debug;
		//self.select_popup.popup.items = [\lfo, \stepper, \performer];
		"nain3.1".debug;

		inner_frame_size = Rect(0,0,self.layout.bounds.width, self.layout.bounds.height-self.header_layout.bounds.height-45);
		self.body_layout = HLayoutView.new(self.layout, inner_frame_size);
		self.update_body;
		"nain4".debug;

		self;
	},

	update_body: { arg self;
		var idx = self.mod_index;
		var main_controller = self.main_controller;
		if(self.body_layout.children.notNil) {
			self.body_layout.children[0].remove;
		};
		switch(self.select_popup.popup.value,
			0, {
				self.body = ~class_lfo_frame.new(self.body_layout, self.body_layout.bounds.extent, idx, main_controller);
			},
			1, {
				self.body = ~class_perfstep_frame.new(self.body_layout, self.body_layout.bounds.extent, idx, main_controller, \performer);
			},
			2, {
				self.body = ~class_perfstep_frame.new(self.body_layout, self.body_layout.bounds.extent, idx, main_controller, \stepper);
			}
		)
	
	}
);

////////// simple gui elements

~class_modmatrix_view = (
	new: { arg self, parent, controller;
		var oscbut;
		var titlebut;

		self.controller = controller;
		
		self.matching = {
			var ret = Dictionary.new;
			[\ring, \phase, \position, \filterfm].do { arg key, idx;
				ret[key] = idx;
			};
			ret;
		}.value;
		self.inv_matching = self.matching.invert;

		self.old_selected_idx = 0 ! 4;
		self.old_selected_kindidx = 0;
		
		titlebut = { arg parent, name, idx;
			var but;
			but = Button.new(parent, 80@15);
			but.states = [
				[name, Color.black, Color.clear],
				[name, Color.black, Color.gray(0.5)],
			];
			but.action = { arg button;
				//self.set_modkind_button(i); // already done in set_property
				self.controller.set_property(\selected_modkind, idx, true); // use controller to update the knob
			};
			but;
		};
		oscbut = { arg parent, name, tidx, idx;
			var but;
			but = Button.new(parent, 20@15);
			but.states = [
				[name, Color.black, Color.clear],
				[name, Color.black, Color.gray(0.5)],
			];
			but.action = { arg button;
				self.set_modmatrix_button(tidx, idx);
				self.controller.set_property(\modvalue, [self.inv_matching[tidx], idx], false);
			};
			but;
		};

		self.main_layout = VLayoutView.new(parent, Rect(0,0,300,200));
		self.line_layouts = ["Ring Mod", "Phase", "Position", "Filter FM"].collect { arg tname, tidx;
			var line_layout;
			line_layout = HLayoutView.new(self.main_layout, Rect(0,0,300,20));
			titlebut.(line_layout, tname, tidx);
			["Off","1","2","3"].do { arg name, idx;
				oscbut.(line_layout, name, tidx, idx) 
			};
			line_layout;
		};
		self.line_layouts[3].children[4].remove;
		~make_class_responder.(self, self.main_layout, self.controller, [ \set_property ]);
	},

	set_modmatrix_button: { arg self, linenum, oscnum;
		self.line_layouts[linenum].children[self.old_selected_idx[linenum]+1].value = 0;
		self.line_layouts[linenum].children[oscnum+1].value = 1;
		self.old_selected_idx[linenum] = oscnum;
	},

	set_modkind_button: { arg self, kindnum;
		self.line_layouts[self.old_selected_kindidx].children[0].value = 0;
		self.line_layouts[kindnum].children[0].value = 1;
		self.old_selected_kindidx = kindnum;
	},

	set_property: { arg self, controller, msg, name, val;
		[name, val].debug("class_modmatrix_view: set_property");
		switch(name,
			\label, { self.name.string = val },
			\selected_modkind, {
				self.set_modkind_button(val);
			},
			\value, { 
				val.keysValuesDo { arg key, oscnum;
					self.set_modmatrix_button(self.matching[key], oscnum);
				};
			}
		)
	}
);

~class_mute_label_view = (
	new: { arg self, parent, size, controller;
		self = self.deepCopy;
		self.controller = controller;
		self.layout = HLayoutView.new(parent, Rect(0,0,size.x,size.y));
		self.mute_button = Button.new(self.layout, Rect(0,0,size.y,size.y));
		self.mute_button.states = [
			["On", Color.green, Color.clear],
			["Off", Color.black, Color.clear]
		];
		self.mute_button.action = {
			self.controller.set_property(\value, self.mute_button.value, false);
		};
		self.name = StaticText.new(self.layout, Rect(0,0,size.x-size.y,size.y));

		~make_class_responder.(self, self.layout, self.controller, [ \set_property ]);
		self;
	},

	set_property: { arg self, controller, msg, name, val;
		switch(name,
			\label, { self.name.string = val },
			\value, { 
				self.mute_button.value = val;
			}
		)
	}
);

~class_mute_view = (
	new: { arg self, parent, size, controller;
		self = self.deepCopy;
		size = size ?? (20@20);
		self.controller = controller;
		self.layout = HLayoutView.new(parent, Rect(0,0,size.x,size.y));
		self.mute_button = Button.new(self.layout, Rect(0,0,size.y,size.y));
		self.mute_button.states = [
			["On", Color.green, Color.clear],
			["Off", Color.black, Color.clear]
		];
		self.mute_button.action = {
			self.controller.set_property(\value, self.mute_button.value, false);
		};
		~make_class_responder.(self, self.layout, self.controller, [ \set_property ]);
		self;
	},

	set_property: { arg self, controller, msg, name, val;
		switch(name,
			\value, { 
				self.mute_button.value = val;
			}
		)
	}
);

~class_popup_view = (
	new: { arg self, parent, size, controller, action;
		self = self.deepCopy;
		self.controller = controller;
		size = size ?? (80@20);

		self.popup = PopUpMenu.new(parent, size);
		self.popup.action = { arg popup;
			action.(popup);
			if(controller.notNil) {
				self.controller.set_property(\value, self.popup.value, false);
			} {
				debug("class_popup_view: controller is nil");
			};
		};
		//[controller.model.uname, controller.menu_items].debug("class_popup_view");
		self.popup.items = try { 
			controller.get_menu_items_names.debug("-----------controller.get_menu_items_names");
		} { 
			["default"]
		};

		~make_class_responder.(self, self.popup, self.controller, [ \set_property ]);
		self;
	},

	set_property: { arg self, controller, msg, name, val;
		[name, val].debug("class_popup_view: set_property");
		switch(name,
			\value, { 
				self.popup.value = val;
			},
			\menu_items, { 
				self.popup.items = controller.get_menu_items_names;
			}
		)
	}
);

~class_mod_slot = (
	val: 0,
	new: { arg self, parent;
		self = self.deepCopy;

		self.text = DragSink.new(parent, Rect(20,00,20,13));
		self.text.background = Color.white;
		self.text.string = "W";
		self.text.font = Font("Helvetica", 9);
		self.text.mouseDownAction={ arg view, x, y, modifier, buttonNumber, clickCount;
			buttonNumber.debug("class_mod_slot: mouseDownAction: buttonNumber");
			if(buttonNumber == 2) {
				self.clear_slot;
			} {
				self.x_offset = x;
				self.val_offset = self.val;
			}
		};
		self.text.mouseUpAction={ arg view, x, y, modifier, buttonNumber, clickCount;
			buttonNumber.debug("class_mod_slot: mouseUpAction: buttonNumber");
			self.update_action;
		};
		self.text.mouseMoveAction = { arg view, x, y;
			var nx, ro;
			nx = x - self.x_offset;
			ro = ((nx/100) + self.val_offset).clip(-0.999, 0.999 );
			[x, y, nx, ro].debug("move");
			self.val = ro;
			self.action;
		};

		self.text.receiveDragHandler = {
			"=======================RRRah".debug;
			View.currentDrag.debug("========= received");
			self.text.string = View.currentDrag[1];
		};
		self.text.canReceiveDragHandler = { true };

		self;
	}
);

~class_slot_column_view = (
	new: { arg self, parent, controller;
		self = self.deepCopy;

		self.vlayout = VLayoutView.new(parent, 40 @ 80);
			self.label = StaticText.new(self.vlayout, Rect(00,00,40,10));
			self.layout = HLayoutView.new(self.vlayout, Rect(0,0,40,80));
				StaticText.new(self.layout, Rect(0,0,5,5)); // spacer
				self.layout = VLayoutView.new(self.layout, Rect(0,0,20,80));
		self.label.string = "bla";
		self.label.font = Font("Helvetica", 8);
		self.slots = 2.collect { arg idx;
			var slot = ~class_mod_slot.new(self.layout);
			slot;
		};
		self.set_controller(controller);

		self;
	},

	set_property: { arg self, controller, msg, name, val;
		var slot;
		[name, val].debug("class_slot_column_view set_property");
		switch(name,
			\label, { self.label.string = val },
			\modulation_source, {
				// val: [slot_idx, modkind, modval]
				slot = self.slots[val[0]].text;
				if(val[1].isNil) {
					slot.string = "";
				} {
					slot.string = val[2];
					switch(val[1],
						\mod, { slot.stringColor = Color.red },
						\macro, { slot.stringColor = Color.green },
						\midi, { slot.stringColor = Color.blue }
					);
				}

			}
		)
	},


	set_controller: { arg self, controller;
		self.controller = controller;

		self.slots.do { arg slot, idx;
			//"slot 2".debug;
			slot.text.receiveDragHandler = {
				controller.set_property(\modulation_source, [idx, View.currentDrag[0], View.currentDrag[1]])
			};
			slot.clear_slot = { arg myself;
				controller.set_property(\modulation_source, [idx, nil, nil]);
			};
		};

		self.responder.remove;
		self.responder = ~make_class_responder.(self, self.layout, self.controller, [ \set_property ]);
	}
);

~class_simple_slider_view = (
	new: { arg self, parent, controller;
		var size;
		self = self.deepCopy;

		self.controller = controller;

		size = 20@160;
		self.knob = Slider.new(parent, Rect(0,0,size.x, size.y));
		self.knob.action = { arg knob;
			self.controller.set_property(\value, knob.value, false);

		};
		~make_class_responder.(self, self.knob, self.controller, [ \set_property ]);

		self;
	},

	set_property: { arg self, controller, msg, name, val;
		var slot;
		[name, val].debug("pknob set_property");
		switch(name,
			\value, { 
				self.knob.value = self.controller.model.norm_val;
			}
		)
	}
);

~class_pknob_view = (
	new: { arg self, parent, size, controller;
		var slot;
		var knobsize;
		self = self.deepCopy;
		//self.vlayout_size = size ?? (180@140);
		self.vlayout_size = 080@115;
		self.text_size_y = 20;
		//knobsize = (self.vlayout_size.x @ (self.vlayout_size.y - (self.text_size_y * 3));
		knobsize = 20@50;


		self.controller = controller;
		self.numslot = try { self.controller.model.numslot } { 3 };


		self.vlayout = VLayoutView.new(parent, (0@0) @ self.vlayout_size);
		//self.vlayout.resize = 0;
		//self.vlayout.background = Color.gray(0.7);
		self.label = StaticText.new(self.vlayout, self.vlayout_size.x @ self.text_size_y);
		self.label.align = \center;
		self.label.string = "Freq";
		"HAHAH 1".debug;
		self.knob = ModKnob.new(self.vlayout, Rect(0,0,knobsize.x,knobsize.y));
		self.knob.keyDownAction = { arg view, char, modifiers, unicode, keycode;
			keycode.debug("class_pknob_view: keyDownAction");
			if(keycode == ~keycodes.enter) {
				~class_edit_number_window.new(nil, controller );
			}

		};
		"HAHAH 2".debug;
		self.val = StaticText.new(self.vlayout, self.vlayout_size.x @ self.text_size_y);
		self.val.string = "45654.54";
		self.val.align = \center;
		self.slot_layout = HLayoutView.new(self.vlayout, self.vlayout_size.x @ 15);
		//self.slot_layout.resize = 4;

		// center the slots
		StaticText.new(self.slot_layout, Rect(0,0,
			if(self.numslot < 3 ) {
					if(self.numslot == 2) { 12 } { 35 }
			} { 5 };
		,15));

		[self.controller.model.uname, self.controller.model.numslot].debug("------- uname, numslot");
		self.slots = self.numslot.collect { arg idx;
			//"slot 2".debug;
			slot = ~class_mod_slot.new(self.slot_layout);
			slot;
		};
		max(0, (3-(self.numslot-1))).do { arg i;
			self.knob.set_range(3-i, nil);
		};

		self.set_controller(self.controller);

		self;
	},

	set_property: { arg self, controller, msg, name, val;
		var slot;
		[name, val].debug("pknob set_property");
		switch(name,
			\label, { self.label.string = val },
			\range, {
				// val: [slot idx, range]
				self.knob.set_range(val[0], val[1]);
				self.knob.refresh;
			},
			\modulation_source, {
				// val: [slot_idx, modkind, modval]
				slot = self.slots[val[0]].text;
				[slot, val].debug("pknob set_property: modulation_source");
				if(val[1].isNil) {
					//slot.stringColor = Color.white;
					slot.string = "";
				} {
					slot.string = (val[2]+1).asString;
					switch(val[1],
						\mod, { slot.stringColor = Color.red },
						\macro, { slot.stringColor = Color.green },
						\midi, { slot.stringColor = Color.blue }
					);
				};
				//slot.string = "A";
				[slot, slot.string, val].debug("pknob set_property: modulation_source");

			},
			\value, { 
				self.knob.value = self.controller.model.norm_val;
				self.val.string = self.controller.model.val.asString;
			}
		)
	},

	set_controller: { arg self, controller;
		self.controller = controller;

		self.slots.do { arg slot, idx;
			//"slot 2".debug;
			slot.text.receiveDragHandler = {
				controller.set_property(\modulation_source, [idx, View.currentDrag[0], View.currentDrag[1]])
			};
			slot.clear_slot = { arg myself;
				controller.set_property(\range, [idx, 0]);
				myself.val = 0;
				controller.set_property(\modulation_source, [idx, nil, nil]);
			};
			slot.action = { arg myself;
				//myself.val.debug("action1");
				//self.knob.set_range(idx, myself.val);
				//self.knob.refresh;
				self.controller.set_property(\range, [idx, myself.val]);
			};
			slot.update_action = { arg myself;
				self.controller.set_property(\update_range, [idx, myself.val]);
			
			}
		};

		self.knob.action = {
			//self.knob.value.debug("knob action: knob value");
			self.controller.set_property(\value, self.knob.value, true);
			//"kiki 0.5".debug;
			//self.val.string = self.controller.get_val.asString;
			//"kiki 1".debug;
		};
		self.controller.model.uname.debug("pknob: set_controller");
		self.controller.refresh;

		self.responder.remove;
		self.responder = ~make_class_responder.(self, self.vlayout, self.controller, [ \set_property ]);
	}
);


~class_pslider_view = (
	parent: ~class_pknob_view,
	new: { arg self, parent, size, controller;
		var slot;
		var is_vertical = true;
		self = self.deepCopy;
		self.vlayout_size = size ?? (080@160);
		//self.vlayout_size = (080@100);
		self.text_size_y = 20;
		self.slot_size_x = 50;
		self.numslot = try { controller.model.numslot } { 3 };

		if(self.vlayout_size.x > self.vlayout_size.y) { 
			is_vertical = false
		};

		if(is_vertical) {

			self.text_size_x = self.vlayout_size.x;
			self.val_size_x = self.vlayout_size.x;
			self.slider_size = self.vlayout_size.x@(self.vlayout_size.y - (self.text_size_y*2) - 10);
		} {
			self.text_size_x = 100;
			self.val_size_x = 50;
			self.slider_size = (self.vlayout_size.x - self.text_size_x - self.val_size_x - self.slot_size_x)@self.vlayout_size.y;
		};
		self.slider_size.debug("******************* slider_size");
		self.vlayout_size.debug("******************* vlayout_size");
		//self.slider_size = self.vlayout_size.x@50;

		self.controller = controller;


		self.vlayout = if(is_vertical) {
			VLayoutView.new(parent, (0@0) @ self.vlayout_size);
		} {
			HLayoutView.new(parent, (0@0) @ self.vlayout_size);
		};
		//self.vlayout.resize = 5;
		self.vlayout.background = Color.gray(0.7);
		self.label = StaticText.new(self.vlayout, self.text_size_x @ self.text_size_y);
		self.label.string = "Freq";

		self.knob = ModSlider.new(self.vlayout, Rect(0,0,self.slider_size.x , self.slider_size.y));

		self.val = StaticText.new(self.vlayout, self.val_size_x @ self.text_size_y);
		self.val.string = "45654.54";
		//self.slot_layout = VLayoutView.new(self.vlayout, self.vlayout_size.x @ (self.text_size_y *1));
		self.slot_layout = VLayoutView.new(self.vlayout, 20 @ self.text_size_y);
		//self.slot_layout = self.vlayout;
		StaticText.new(self.slot_layout, Rect(0,0,5,1)); // spacer
		//self.slot_layout = HLayoutView.new(self.slot_layout, 20 @ (self.text_size_y *3));
		self.slot_layout = HLayoutView.new(self.slot_layout, 20 @ self.text_size_y-5);
		//self.slot_layout.resize = 4;

		self.slots = self.numslot.collect { arg idx;
			slot = ~class_mod_slot.new(self.slot_layout);
			slot.action = { arg myself;
				//myself.val.debug("action1");
				self.knob.set_range(idx, myself.val);
				self.knob.rangeview.refresh;
				self.controller.set_property(\range, [idx, myself.val], false);
			};
			slot;
		};
		max(0, (3-(self.numslot-1))).do { arg i;
			self.knob.set_range(3-i, nil);
		};

		self.set_controller(self.controller);

		self;
	}
);

~class_edit_number_view = (
	new: { arg self, parent, size, ctrl, close_fun;
		self = self.deepCopy;
		size = size ?? Rect(50,20,100,30);
		self.box = NumberBox(parent, size);
		if(ctrl.notNil) {
			self.box.value = ctrl.get_val;
			self.box.action = { arg number;
				number.value.debug("class_edit_number_view: number");
				ctrl.set_val(number.value);
				ctrl.changed(\set_property, \value, ctrl.get_norm_val);
				
			};
			self.box.clipLo = ctrl.model.spec.clipLo;
			self.box.clipHi = ctrl.model.spec.clipHi;

			self.box.keyDownAction = { arg view, char, modifiers, unicode, keycode;
				"class_pknob_view: keyDownAction".debug;
				if(keycode == ~keycodes.escape) {
					close_fun.()
				};
				self.box.defaultKeyDownAction(char, modifiers, unicode, keycode);

			};
		};

		self;
	}

);

~class_edit_number_window = (
	new: { arg self, size, ctrl;
		var title;
		self = self.deepCopy;
		title = if(ctrl.notNil) { ctrl.model.uname } { "Edit number" };
		self.window = Window.new(title, Rect(0,0,200,100));
		self.child = ~class_edit_number_view.new(self.window, nil, ctrl, { self.window.close });
		self.window.front;

		self;
	}

);



////////// frames gui elements


~class_frame_view = (
	make_frame: { arg self, parent, frame_size, mute_ctrl, popup_ctrl, fader_ctrl;
		var hsize = 20;
		var body_size = (frame_size.x-30)@(frame_size.y-hsize);

		frame_size = frame_size ?? (080@155);

		self.frame_layout = VLayoutView.new(parent, Rect(0,0,frame_size.x,frame_size.y));
		self.frame_layout.background = Color.gray(0.7);
		self.header_layout = HLayoutView.new(self.frame_layout, Rect(0,0,body_size.x,hsize));

		self.frame_name = ~class_mute_label_view.new(self.header_layout, (body_size.x/2)@hsize, mute_ctrl);
		if(popup_ctrl.notNil) {
			self.header_popup = ~class_popup_view.new(self.header_layout, (body_size.x/2)@hsize, popup_ctrl);
		};

		self.outer_body_layout = HLayoutView.new(self.frame_layout, Rect(0,0,frame_size.x,body_size.y));
		HLayoutView.new(self.outer_body_layout, Rect(0,0,5,5)); // spacer
		self.body_layout = HLayoutView.new(self.outer_body_layout, Rect(0,0,body_size.x,body_size.y));
		self.body_layout.background = Color.gray(0.5);
		
		if(fader_ctrl.notNil) {
			self.frame_fader = ~class_simple_slider_view.new(self.outer_body_layout, fader_ctrl);
		};

		HLayoutView.new(self.frame_layout, Rect(0,0,5,5)); // spacer

	}
);

~class_knobs_frame_view = (
	new: { arg self, parent, frame_size, knobs, mute, popup, fader;
		// controller_names: [mute, popup, arg1, arg2, fader]
		self = self.deepCopy;
		frame_size = frame_size ?? (350@145);

		"class_knobs_frame_view0".debug;
		~class_frame_view[\make_frame].(self, parent, frame_size, mute, popup, fader);
		"class_knobs_frame_view1".debug;

		self.knobs = knobs.do { arg ctrl;
			ctrl.model.uname.debug("class_knobs_frame_view..");
			~class_pknob_view.new(self.body_layout, nil, ctrl);
		};
		"class_knobs_frame_view2".debug;

		self;
	}
);

~class_modosc_frame_view = (
	// TODO
	new: { arg self, parent, frame_size, controllers, mute;
		// controller_names: [mute, popup, arg1, arg2, fader]
		self = self.deepCopy;
		frame_size = frame_size ?? (550@155);
		self.matrix_controller = controllers[5];
		self.controllers = controllers;

		~class_frame_view[\make_frame].(self, parent, frame_size, mute);
		self.body_layout.debug("SQUOIIII ce bordel0");

		self.body_layout.debug("SQUOIIII ce bordel1");
		self.pitch_knob = ~class_pknob_view.new(self.body_layout, nil, controllers[0]);
		self.body_layout.debug("SQUOIIII ce bordel2");
		self.mod_knob = ~class_pknob_view.new(self.body_layout, nil, controllers[1]);
		self.body_layout.debug("SQUOIIII ce bordel3");
		self.matrix = ~class_modmatrix_view.new(self.body_layout, self.matrix_controller);

		"SQUOIIII ce bordel".debug;
		self.body_layout.debug("SQUOIIII ce bordel4");
		~make_class_responder.(self, self.body_layout, self.matrix_controller, [ \set_property ]);

		self;
	},

	set_property: { arg self, controller, msg, name, val;
		var slot;
		[name, val].debug("modosc frame set_property");
		switch(name,
			\selected_modkind, { 
				self.mod_knob.set_controller(self.controllers[1+val]);
			}
		)
	}
);

~class_amp_view = (
	new: { arg self, parent, slot, pan, mute;
		// controller_names: [mute, amp_slot_column, pan]
		var frame_size;
		self = self.deepCopy;
		frame_size = 140@165;

		~class_frame_view[\make_frame].(self, parent, frame_size, mute);

		self.amp_slots = ~class_slot_column_view.new(self.body_layout, slot);
		self.pan_knob = ~class_pknob_view.new(self.body_layout, nil, pan);

		self;
	}
);

~class_bypass_view = (
	new: { arg self, parent, slot, mute, fader;
		// controller_names: [mute, bypass_slot_column, bypass_fader]
		var frame_size;
		self = self.deepCopy;
		frame_size = 080@155;

		~class_frame_view[\make_frame].(self, parent, frame_size, mute, nil, fader);

		self.bypass_slots = ~class_slot_column_view.new(self.body_layout, slot);

		self;
	}
);

~class_master_view = (
	new: { arg self, parent, controller, mute;
		// controller_names: [mute, master]
		var frame_size;
		self = self.deepCopy;
		frame_size = 110@155;

		~class_frame_view[\make_frame].(self, parent, frame_size, mute);

		self.master_knob = ~class_pknob_view.new(self.body_layout, nil, controller);

		self;
	}
);

~class_masterfx_view = (
	// TODO
	new: { arg self, parent, fx1_ctrl, fx2_ctrl, eq_ctrl;
		var frame_size;
		self = self.deepCopy;
		frame_size = 350@155;

		self.make_frame(parent, frame_size, fx1_ctrl, fx2_ctrl, eq_ctrl);
		self.knobs = 4.collect { arg idx;
			~class_pknob_view.new(self.body_layout, nil, fx1_ctrl[idx]);
		};
		self.set_current_tab(fx1_ctrl);
		self.frame_name.value = 1;

		self;
	},

	set_current_tab: { arg self, ctrl;
		self.knobs.do { arg knob, i;
			knob.set_controller(ctrl[i]);
		}
	},

	make_frame: { arg self, parent, frame_size, fx1_ctrl, fx2_ctrl, eq_ctrl;
		var hsize = 20;
		var labelsizex = 20;
		var name;
		var body_size = (frame_size.x-30)@(frame_size.y-hsize);
		var fx1_mute = fx1_ctrl[4];
		var fx2_mute = fx2_ctrl[4];
		var fx1_popup_ctrl = fx1_ctrl[5];
		var fx2_popup_ctrl = fx2_ctrl[5];
		var eq_mute = eq_ctrl[4];

		frame_size = frame_size ?? (080@155);

		"class_masterfx_view: make_frame: 0".debug;
		self.frame_layout = VLayoutView.new(parent, Rect(0,0,frame_size.x,frame_size.y));
		self.frame_layout.background = Color.gray(0.7);
		self.header_layout = HLayoutView.new(self.frame_layout, Rect(0,0,body_size.x,hsize));

		////////// tab FX1

		self.mute1 = ~class_mute_view.new(self.header_layout, nil, fx1_mute);
		self.frame_name = Button.new(self.header_layout, labelsizex@hsize);
		name = "Fx1";
		self.frame_name.states = [
			[name, Color.black, Color.clear],
			[name, Color.black, Color.gray(0.5)],
		];
		self.frame_name.action = {
			self.set_current_tab(fx1_ctrl);
			self.frame_name.value = 1;
			self.frame_name2.value = 0;
			self.frame_name3.value = 0;
		};
		"class_masterfx_view: make_frame: 1".debug;
		if(fx1_popup_ctrl.notNil) {
			self.header_popup = ~class_popup_view.new(self.header_layout, (body_size.x/4)@hsize, fx1_popup_ctrl);
		};

		////////// tab FX2

		self.mute2 = ~class_mute_view.new(self.header_layout, nil, fx2_mute);
		self.frame_name2 = Button.new(self.header_layout, labelsizex@hsize);
		name = "Fx2";
		self.frame_name2.states = [
			[name, Color.black, Color.clear],
			[name, Color.black, Color.gray(0.5)],
		];
		self.frame_name2.action = {
			self.set_current_tab(fx2_ctrl);
			self.frame_name.value = 0;
			self.frame_name2.value = 1;
			self.frame_name3.value = 0;
		};
		if(fx2_popup_ctrl.notNil) {
			self.header_popup = ~class_popup_view.new(self.header_layout, (body_size.x/4)@hsize, fx2_popup_ctrl);
		};

		////////// tab EQ

		"class_masterfx_view: make_frame: 5".debug;
		self.mute3 = ~class_mute_view.new(self.header_layout, nil, eq_mute);
		self.frame_name3 = Button.new(self.header_layout, labelsizex@hsize);
		name = "Eq";
		self.frame_name3.action = {
			self.set_current_tab(eq_ctrl);
			self.frame_name.value = 0;
			self.frame_name2.value = 0;
			self.frame_name3.value = 1;
		};
		self.frame_name3.states = [
			[name, Color.black, Color.clear],
			[name, Color.black, Color.gray(0.5)],
		];
	
		/////////// body

		self.outer_body_layout = HLayoutView.new(self.frame_layout, Rect(0,0,frame_size.x,body_size.y));
		HLayoutView.new(self.outer_body_layout, Rect(0,0,5,5)); // spacer
		self.body_layout = HLayoutView.new(self.outer_body_layout, Rect(0,0,body_size.x,body_size.y));
		self.body_layout.background = Color.gray(0.5);
		
		HLayoutView.new(self.frame_layout, Rect(0,0,5,5)); // spacer

	}
);

~class_routing_view = (
	new: { arg self, parent, sizerect, main_controller;
		var ctrl = { arg name; main_controller.get_arg(("routing_"++name).asSymbol) };
		var row;
		sizerect = sizerect.asRect;
		self = self.deepCopy;
		self.main_controller = { arg self; main_controller };

		self.layout = VLayoutView.new(parent, sizerect);
		self.ctrl_names = [\insert1, \insert2, \feedback, \bypass_osc, \bypass_dest];

		self.ctrl_names.do { arg ctrlname, idx;
			row = HLayoutView.new(self.layout, Rect(0,0,sizerect.width,30));
			StaticText.new(row, Rect(0,0,150,30)).string_(ctrl.(ctrlname).model.name);
			self[ctrlname] = ~class_popup_view.new(row, Rect(0,0,150,30), ctrl.(ctrlname));
		};

		self;
	}
);

~class_voicing_view = (
	new: { arg self, parent, sizerect, main_controller;
		var ctrl = { arg name; main_controller.get_arg(("voicing_"++name).asSymbol) };
		var row;
		sizerect = sizerect.asRect;
		self = self.deepCopy;
		self.main_controller = { arg self; main_controller };

		self.layout = HLayoutView.new(parent, sizerect);
		self.left_layout = VLayoutView.new(self.layout, Rect(0,0,80,sizerect.height));
		self.left_layout.background = Color.red;

		ctrl.(\pitch_spread).debug("model!!!!!!!!!");

		self.voice_max = ~class_edit_number_view.new(self.left_layout, nil, ctrl.(\unisono));
		self.voice_unisono = ~class_edit_number_view.new(self.left_layout, nil, ctrl.(\unisono));

		self.mono_button = Button.new(self.left_layout, self.left_layout.bounds.width@20);
		self.mono_button.states = [
			["Polyphony"],
			["Monophony"],
		];

		self.trigger_button = Button.new(self.left_layout, self.left_layout.bounds.width@20);
		self.trigger_button.states = [
			["Always"],
			["Legato"],
			["Legato Thriller"],
		];

		( sizerect.width - self.left_layout.bounds.width ).debug("size!!!!!!!!!!!!");
		self.right_layout = VLayoutView.new(self.layout, Rect(0,0, sizerect.width - self.left_layout.bounds.width, sizerect.height));

		[\pitch, \wavetable, \pan].do { arg name;
			var layout, onoff, label, lorange, slider, hirange;
			var myctrl = { arg na; ctrl.((name++na).asSymbol) };

			layout = HLayoutView.new(self.right_layout, Rect(0,0,self.right_layout.bounds.width, 40));
			layout.background = Color.blue;

			onoff = Button.new(layout, 30@30);
			onoff.states = [
				["On"],
				["Off"],
			];
			onoff.value = ctrl.(("enable_"++name).asSymbol).model.val;
			onoff.action = { arg but;
				ctrl.(("enable_"++name).asSymbol).set_property(\value, but.value);
			};
			//label = StaticText.new(layout, 120@20);
			//label.string_(main_controller.get_arg(( name++"_spread" ).asSymbol).model.name);

			lorange = ~class_edit_number_view.new(layout, Rect(0,0,50,30), myctrl.("_lorange"));

			slider = ~class_pslider_view.new(layout, (layout.bounds.width - 300)@layout.bounds.height, main_controller.get_arg(( name++"_spread" ).asSymbol));

			hirange = ~class_edit_number_view.new(layout, Rect(0,0,50,30), myctrl.("_hirange"));
		};

		self;
	}
);

~class_saveload_view = (
	new: { arg self, parent, sizerect, ctrl;
		var row;
		self = self.deepCopy;
		sizerect = sizerect.asRect;

		self.layout = VLayoutView.new(parent, sizerect);
		self.layout = HLayoutView.new(self.layout, Rect(0,0,sizerect.width,30));

		self.popup = ~class_popup_view.new(self.layout, Rect(0,0,150,30), ctrl);

		self.bt_previous = Button.new(self.layout, Rect(0,0,30,30));
		self.bt_previous.states = [["<"]];
		self.bt_previous.action = {
			ctrl.previous_preset;
		};
		self.bt_next = Button.new(self.layout, Rect(0,0,30,30));
		self.bt_next.states = [[">"]];
		self.bt_next.action = {
			ctrl.next_preset;
		};

		self.bt_save_as = Button.new(self.layout, Rect(0,0,60,30));
		self.bt_save_as.states = [["Save as"]];
		self.bt_save_as.action = {
			var action = { arg uname;
				"action!!".debug;
				ctrl.save_current_preset_as_uname(uname);
			};
			~class_save_preset_dialog.new(ctrl.get_menu_items_names, action);
		};


		self;
	}

);

~class_center_frame_view = (
	new: { arg self, parent, sizerect, main_controller;
		var makebut;
		var old_selected_idx = [0,0];
		self.main_controller = { arg self; main_controller };
		self = self.deepCopy;

		"class_center_frame_view.new: 0".debug;

		makebut = { arg parent, name, idx, kind;
			var but;
			var drag;
			but = Button.new(parent, Rect(0,0,60,15));
			but.states = [
				[ name, Color.black, Color.clear ],
				[ name, Color.black, Color.gray(0.5) ],
			];
			but.action = { arg button;
				self.tab_layout.children[old_selected_idx[0]].value = 0;
				self.modenv_layout.children[old_selected_idx[1]*2].value = 0;
				if(kind == 0) {
					parent.children[idx].value = 1;
				} {
					parent.children[idx*2].value = 1;
				};
				old_selected_idx[kind] = idx;
				if(kind == 0) {
					self.body_layout.children[0].remove;
					self.body = switch(idx,
						3, { ~class_voicing_view.new(self.body_layout, self.body_size, main_controller) },
						4, { ~class_routing_view.new(self.body_layout, self.body_size, main_controller) },
						5, { ~class_saveload_view.new(self.body_layout, self.body_size, main_controller.get_arg(\presets_global)) },
						{ ~class_routing_view.new(self.body_layout, self.body_size, main_controller) }
					);
				} {
					self.body_layout.children[0].remove;
					self.body = case
						{ idx < 4 } { ~class_env_edit_view.new(self.body_layout, self.body_size, idx, main_controller) }
						{ idx >= 4 } { ~class_lfoperfstep_frame.new(self.body_layout, self.body_size, idx-4, main_controller) }
					;

				}
			};
			if(kind == 1) {
				drag = DragSource.new(parent, Rect(0,0,10,10));
				drag.object = [\mod, idx];
			};
			//drag.string = "+";

		};

		"class_center_frame_view.new: 1".debug;
		self.layout = VLayoutView.new(parent, sizerect);
		self.layout.background = Color.gray(0.9);
		self.body_size = Point(self.layout.bounds.width,self.layout.bounds.height-00);

		self.tab_layout = HLayoutView.new(self.layout, Rect(0,0,self.layout.bounds.width,20));
		["Osc", "Ktr Osc", "Ktr Flt", "Voicing", "Routing", "Global"].do { arg name, i;
			makebut.(self.tab_layout, name, i, 0);
		};
		"class_center_frame_view.new: 2".debug;
		self.modenv_layout = HLayoutView.new(self.layout, Rect(0,0,self.layout.bounds.width,20));
		["Env1", "Env2", "Env3", "Env4", "LFO1", "LFO2", "LFO3", "LFO4"].do { arg name, i;
			makebut.(self.modenv_layout, name, i, 1);
		};
		"class_center_frame_view.new: 3".debug;
		self.body_layout = HLayoutView.new(self.layout, Rect(0,0,self.body_size.x,self.body_size.y));
		self.body = ~class_env_edit_view.new(self.body_layout, self.body_size, 0, main_controller);
		"class_center_frame_view.new: fin".debug;

		self;
	}

);

~class_macro_frame_view = (

	new: { arg self, parent, main_controller, controllers;
		var makebut, makeknob;
		var bounds;

		"MACRO".debug;

		makebut = { arg parent, name;
			var text;
			text = DragSource.new(parent, Rect(0,0,parent.bounds.width/2,10));
			text.object = [\midi, \velocity];
			//text.background = Color.yellow;
			text.string = name;
		};

		makeknob = { arg parent, idx, ctrl, name;
			var bounds = parent.bounds, tsize = 20;
			var klayout = VLayoutView.new(parent, Rect(0,0,bounds.width/5,bounds.height-5));
			var tlayout;
			var drag, text;
			var ret = ();
			bounds = klayout.bounds;
			ret.knob = Knob.new(klayout, Rect(0,0,bounds.width,bounds.height-tsize));
			ret.knob.value = 1;
			ret.knob.action = { arg knob;
				ctrl.set_property(\value, knob.value, false);
			};
			tlayout = HLayoutView.new(klayout, Rect(0,0,bounds.width-10,tsize));
			text = TextField.new(tlayout, Rect(0,0,bounds.width-20,tsize));
			text.string = "Maciro " ++ idx;
			text.font = Font.new("Helvetica", 8);
			drag = DragSource.new(tlayout, Rect(0,0,10,tsize));
			drag.object = [\macro, idx];
			//drag.background = Color.yellow;
			drag.string = "";

			ret.debug("RET");
			ret.label = text;

			ret;

		};

		self = self.deepCopy;

		self.macros = List.new;
		self.controllers = controllers;

		bounds = parent.bounds;
		self.layout = HLayoutView.new(parent, Rect(0,0,500,bounds.height));
		//self.layout.background = Color.yellow;

		self.keyboard_layout = VLayoutView.new(self.layout, Rect(0,0,100,100));
		bounds = self.keyboard_layout.bounds;
		self.keyboard_sublayout1 = HLayoutView.new(self.keyboard_layout, Rect(0,0,bounds.width,bounds.height/2));
		makebut.(self.keyboard_sublayout1, "KTr");
		makebut.(self.keyboard_sublayout1, "Vel");
		self.keyboard_sublayout2 = HLayoutView.new(self.keyboard_layout, Rect(0,0,bounds.width,bounds.height/2));
		makebut.(self.keyboard_sublayout2, "AT");
		makebut.(self.keyboard_sublayout2, "TrR");

		bounds = self.layout.bounds;
		self.macro_layout = VLayoutView.new(self.layout, Rect(0,0,bounds.width-self.keyboard_layout.bounds.width-60,bounds.height));
		bounds = self.macro_layout.bounds;
		self.macro_sublayout1 = HLayoutView.new(self.macro_layout, Rect(0,0,bounds.width,bounds.height/2));
		4.do { arg idx;
			self.macros.add(makeknob.(self.macro_sublayout1, idx, controllers[idx]));
			self.macros.debug("macros ddd");
			~make_class_responder.(self, self.layout, controllers[idx], [ \set_property ]);
		};
		self.macro_sublayout2 = HLayoutView.new(self.macro_layout, Rect(0,0,bounds.width,bounds.height/2));
		4.do { arg idx;
			self.macros.add(makeknob.(self.macro_sublayout2, idx+4, controllers[idx+4]));
			self.macros.debug("macros eee");
			~make_class_responder.(self, self.layout, controllers[idx+4], [ \set_property ]);
		};


		self;
	},

	set_property: { arg self, controller, msg, name, val;
		var idx;
		idx = self.controllers.indexOf(controller);
		[idx, name, val].debug("macro frame set_property");
		self.macros.debug("macros");
		switch(name,
			\value, { 
				self.macros[idx].knob.value = val;
			},
			\label, {
				self.macros[idx].label.string = val;
			}
		)
	}

);

~class_load_curve_frame = (
	new: { arg self, parent, size, curvebank, curvelist, curve_edit;
		var line_layout;
		var curveview;
		self = self.deepCopy;

		self.layout = VLayoutView.new(parent, size.asRect);

		curvelist.clump(4).do { arg curverow;
			line_layout = HLayoutView.new(self.layout, Rect(0,0,size.x, 50));
			curverow.do { arg curvename;
				curveview = ~class_mini_curvegraph_view.new(line_layout, 50@50,curvebank, curvename, curve_edit);
			}
		};

		self;
	};
);


////////// main gui


~class_passive_view2 = (
	new: { arg self, controller;
		var controllers;
		var frame_size;
		var block_size = 300@300;
		var get_controllers = { arg id;
			self.controller.paramdata[id].collect { arg data; self.controller.get_arg(data.uname) };
		};
		self = self.deepCopy;
		self.controller = controller;
		

		"hehehe1".debug;
		self.window = Window.new("Passive", Rect(0,0,1400,800));
		self.window.front;
		self.main_layout = HLayoutView.new(self.window, self.window.view.bounds);

		~class_voicing_view.new(self.main_layout, Rect(0,0,1000,300), controller);

		self;
	}
);


~class_passive_view = (
	new: { arg self, controller;
		var controllers;
		var frame_size;
		var block_size = 300@300;
		var get_controllers = { arg id;
			self.controller.paramdata[id].collect { arg data; self.controller.get_arg(data.uname) };
		};
		self = self.deepCopy;
		self.controller = controller;
		

		"hehehe1".debug;
		self.window = Window.new("Passive", Rect(0,0,1400,800));
		self.window.front;
		self.main_layout = HLayoutView.new(self.window, self.window.view.bounds);

		self.gen_layout = VLayoutView.new(self.main_layout, Rect(0,0,400,800));

		"hehehe1.1".debug;
			controllers = get_controllers.(\osc1);
			self.osc1 = ~class_knobs_frame_view.new(self.gen_layout, nil, controllers[0..3], controllers[4], controllers[5], controllers[6]);
			controllers = get_controllers.(\osc2);
			self.osc2 = ~class_knobs_frame_view.new(self.gen_layout, nil, controllers[0..3], controllers[4], controllers[5], controllers[6]);
			controllers = get_controllers.(\osc3);
			self.osc3 = ~class_knobs_frame_view.new(self.gen_layout, nil, controllers[0..3], controllers[4], controllers[5], controllers[6]);
		"hehehe1.2".debug;

			controllers = get_controllers.(\modosc);
			self.modosc = ~class_modosc_frame_view.new(self.gen_layout, nil, controllers[0..5], controllers[6]);

			self.noiseback_layout = HLayoutView.new(self.gen_layout, Rect(0,0,400,200));
				
		"hehehe1.3".debug;
				controllers = get_controllers.(\noise);
				frame_size = 200@150;
				self.noise = ~class_knobs_frame_view.new(self.noiseback_layout, frame_size, controllers[0..1], controllers[2], controllers[3], controllers[4]);

		"hehehe1.4".debug;
				controllers = get_controllers.(\feedback);
				self.feedback = ~class_knobs_frame_view.new(self.noiseback_layout, frame_size, [controllers[0]], controllers[1], nil, controllers[2]);

		"hehehe2".debug;
	  self.right_layout = VLayoutView.new(self.main_layout, Rect(0,0,1000,1000));

			self.top_layout = HLayoutView.new(self.right_layout, Rect(0,0,1000,300));

				self.filter_layout = HLayoutView.new(self.top_layout, Rect(0,0,400,300));

		"hehehe2.1".debug;
					frame_size = 50@(260);
					self.parseq = ~class_pslider_view.new(self.filter_layout, frame_size, self.controller.get_arg(\filter_parseq));
		"hehehe2.2".debug;

					self.filter_frames_layout = VLayoutView.new(self.filter_layout, Rect(0,0,300,300));

		"hehehe3".debug;
						controllers = get_controllers.(\filter1);
						frame_size = 300@150;
						self.filter1 = ~class_knobs_frame_view.new(self.filter_frames_layout, frame_size,
							controllers[0..2], controllers[3], controllers[4], controllers[5]
						);
						controllers = get_controllers.(\filter2);
						self.filter1 = ~class_knobs_frame_view.new(self.filter_frames_layout, frame_size,
							controllers[0..2], controllers[3], controllers[4], controllers[5]
						);

		"hehehe4".debug;
					frame_size = 50@(260);
					self.filtermix = ~class_pslider_view.new(self.filter_layout, frame_size, self.controller.get_arg(\filter_mix));

				self.master_layout = VLayoutView.new(self.top_layout, Rect(0,0,700,300));

					self.master_amp_layout = HLayoutView.new(self.master_layout, Rect(0,0,300,150));
						
		"hehehe5".debug;
						controllers = get_controllers.(\master_pan);
						self.master_pan = ~class_amp_view.new(self.master_amp_layout, controllers[0], controllers[1], controllers[2]);
		"hehehe5.1".debug;
						controllers = get_controllers.(\bypass);
						self.bypass = ~class_bypass_view.new(self.master_amp_layout, controllers[0], controllers[1], controllers[2]);
		"hehehe5.2".debug;
						controllers = get_controllers.(\master);
						self.master = ~class_master_view.new(self.master_amp_layout, controllers[0], controllers[1]);
		"hehehe6".debug;

					self.master_fx_layout = HLayoutView.new(self.master_layout, Rect(0,0,300,300));

						// TODO
						self.masterfx = ~class_masterfx_view.new(self.master_fx_layout, 
							get_controllers.(\fx1),
							get_controllers.(\fx2),
							get_controllers.(\eq),
						);

			self.center_frame = ~class_center_frame_view.new(self.right_layout, Rect(0,0,1000,300), self.controller);

		"hehehe7".debug;
			self.bottom_layout = HLayoutView.new(self.right_layout, Rect(0,0,1000,135));
			//self.bottom_layout.background = Color.red;
				
				controllers = get_controllers.(\insert1);
				frame_size = 200@200;
				self.insertfx1 = ~class_knobs_frame_view.new(self.bottom_layout, frame_size, controllers[0..1], controllers[2], controllers[3]);
				controllers = get_controllers.(\insert2);
				self.insertfx2 = ~class_knobs_frame_view.new(self.bottom_layout, frame_size, controllers[0..1], controllers[2], controllers[3]);
				controllers = get_controllers.(\macro);
				self.macro = ~class_macro_frame_view.new(self.bottom_layout, self.main_controller, controllers);
		"hehehe8".debug;


		self;
	}
);


