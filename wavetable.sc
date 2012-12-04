
~class_wavetable_file = (
	new: { arg self;
		self = self.deepCopy;

		self;
	},
	
	new_from_data: { arg self, data;
		if(data.class == String) {
			~class_wavetable_sample_file.new(data);
		} {
			//TODO: curvebank lib
			~class_wavetable_sigfunc_file.new(~curvebank, data[1]);
		}
	
	},

	label: { arg self;
	
	},


	folders: { arg self;
	
	},

	files: { arg self;
	
	},

	load_in_wavetable_buffer: { arg self, buffer;
	
	},

	as_signal: { arg self;
	
	}

);

~class_wavetable_sample_file = (
	new: { arg self, path;
		self = self.deepCopy;
		"huuu1".debug;
		self.pathname = PathName.new(path);
		self.file_kind = \sample;
		if(self.pathname.isFolder) {
			self.kind = \folder;
		} {
			self.kind = \file;
		};
		self;
	},

	save_data: { arg self;
		// TODO: when multiples curvebank, write a curvebank library
		self.pathname.fullPath;
	},

	label: { arg self;
		if(self.kind == \folder) {
			self.pathname.folderName;
		} {
			self.pathname.fileName;
		}
	},


	folders: { arg self;
		self.pathname.folders.collect { arg x; ~class_wavetable_sample_file.new(x.fullPath) };
	},

	files: { arg self;
		self.pathname.files.collect { arg x; ~class_wavetable_sample_file.new(x.fullPath) };
	},

	load_in_wavetable_buffer: { arg self, buffer;
		var path;
		if(self.kind == \file) {
			path = self.pathname.fullPath;
			~load_sample_in_wavetable_buffer.(buffer, path);
		};
	},

	load_in_signal: { arg self, signal;
		var sf, sig;
		if(self.kind == \file) {
			sf = SoundFile.openRead(self.pathname.fullPath);
			sf.readData(signal);
			sf.close;
			signal;
		}
	},

	as_sigfunc: { arg self, signal;
		if(self.kind == \file) {
			~load_sample_as_sigfunc.(self.pathname.fullPath);
		}
	}

);

~class_wavetable_sigfunc_file = (
	new: { arg self, bank, funcname;
		self = self.deepCopy;
		self.bank = bank;
		self.funcname = funcname;
		self.file_kind = \sigfunc;
		if(funcname.isNil) {
			self.kind = \folder;
		} {
			self.kind = \file;
		};
		self;
	},

	label: { arg self;
		self.debug("class_wavetable_sigfunc_file: label");
		if(self.kind == \folder) {
			self.bank.name ?? "Nameless sigbank";
		} {
			self.funcname
		}
	},

	save_data: { arg self;
		// TODO: when multiples curvebank, write a curvebank library
		[self.label, self.funcname];
	},


	folders: { arg self;
		// sigbank dont implemented sub-banks
		[]
	},

	files: { arg self;
		if(self.kind == \folder) {
			self.bank[\get_keys].(self.bank).collect { arg name;
				~class_wavetable_sigfunc_file.new(self.bank, name);
			}
		} {
			[]
		};
	},

	load_in_wavetable_buffer: { arg self, buffer;
		if(self.kind == \file) {
			~load_curve_in_wavetable_buffer.(buffer, self.bank[self.funcname]);
		};
	},

	load_in_signal: { arg self, signal;
		var sf, sig;
		if(self.kind == \file) {
			sig = signal.waveFill(self.bank[self.funcname]);
			sig;
		}
	},

	as_sigfunc: { arg self, signal;
		if(self.kind == \file) {
			self.bank[self.funcname];
		}
	}

);


~class_load_wavetable_dialog = (
	apply_done: false,
	new: { arg self, apply_action, cancel_action, path, single=false;
		var wtpath, librarylist, folderlist, filelist, filepath, selectedlist = List.new, selectedfile;
		self = self.deepCopy;
		"ciiion0".debug;

		self.numframes = 4096*2;
		self.buffer = Buffer.alloc(s, self.numframes);
		self.signal = Signal.newClear(self.numframes/2);

		"Merde".postln;
		wtpath = path ?? "~/Musique/archwavetable/Architecture Waveforms 2010 Wav24/Architecture Waveforms 2010 Wav24".standardizePath;
		librarylist = [
			~class_wavetable_sample_file.new(wtpath),
			~class_wavetable_sigfunc_file.new(~curvebank),
		];
		//folderlist = PathName.new(wtpath).folders;
		folderlist = [];
		"con0".debug;

		self.window = Window.new("choose wavetable", Rect(0,0,1300,400));
		self.window.onClose = {
			self.buffer.free;
			self.synthnode.release;
			if(self.apply_done.not) {
				cancel_action.();
			}
		};
		self.layout = HLayoutView.new(self.window, Rect(0,0,1300,400));

		"con1".debug;
		self.librarylistview = ListView.new(self.layout, Rect(0,0,300,400));
		self.librarylistview.items = librarylist.collect{ arg folder; folder.label };
		self.librarylistview.action = { arg view, b, c;
			[a, b, c].debug("libaction");
			folderlist = librarylist[view.value].folders;
			librarylist[view.value].debug("library elm");
			folderlist.debug("folderlist");
			self.folderlistview.items = folderlist.collect { arg file; file.label };
			self.folderlistview.value = 0;
			self.folderlistview.action.value(self.folderlistview);
		};

		"con2".debug;
		self.folderlistview = ListView.new(self.layout, Rect(0,0,300,400));
		self.folderlistview.items = folderlist.collect{ arg folder; folder.label };
		self.folderlistview.action = { arg view, b, c;
			[a, b, c].debug("folderaction");
			if(folderlist[view.value].notNil) {
				filelist = folderlist[view.value].files;
				self.filelistview.items = filelist.collect { arg file; file.label };
				self.filelistview.value = 0;
				self.filelistview.action.value(self.filelistview);
			} {
				if(librarylist[self.librarylistview.value].notNil) {
					filelist = librarylist[self.librarylistview.value].files;
					self.filelistview.items = filelist.collect { arg file; file.label };
					self.filelistview.value = 0;
					self.filelistview.action.value(self.filelistview);
				}

			}
		};

		"con3".debug;
		self.filelistview = ListView.new(self.layout, Rect(0,0,300,400));
		//self.filelistview.items = ["bla", "rah"];
		self.filelistview.action = { arg view, b, c;
			[view, b, c].debug("filelist action");
			selectedfile = filelist[view.value];
			self.display_file(selectedfile);
		};

		self.right_layout = VLayoutView.new(self.layout, Rect(0,0,300,400));

		self.buttons_layout = HLayoutView.new(self.right_layout, Rect(0,0,300,20));

		"con4".debug;
		self.but_play = Button.new(self.buttons_layout, Rect(0,0,50,20));
		self.but_play.string = "Play";
		self.but_play.action = {
			self.synthnode.debug("synth");
			if(self.synthnode.isNil.debug("isNil")) {
				self.displayed_file.load_in_wavetable_buffer(self.buffer);
				self.synthnode = { Osc.ar(self.buffer, MouseX.kr(20, 380), mul:0.02) ! 2  }.play;
				self.but_play.string = "Stop";
			} {
				"iou".debug;
				self.synthnode.release;
				self.synthnode = nil;
				self.but_play.string = "Play";
			}
		};

		"con5".debug;
		self.but_add = Button.new(self.buttons_layout, Rect(0,0,50,20));
		self.but_add.string = "+ Add";
		self.but_add.action = {
			if(single) {
				selectedlist = [selectedfile];
			} {
				selectedlist.add(selectedfile);
			};
			self.selectedlistview.items = selectedlist.collect(_.label);
		};

		self.but_add = Button.new(self.buttons_layout, Rect(0,0,50,20));
		self.but_add.string = "- Rem";
		self.but_add.action = {
			selectedlist.removeAt(self.selectedlistview.value);
			self.selectedlistview.items = selectedlist.collect(_.label);
		};

		StaticText.new(self.buttons_layout, Rect(0,0,10,20)); //spacer

		"con6".debug;
		self.but_movedown = Button.new(self.buttons_layout, Rect(0,0,50,20));
		self.but_movedown.string = "Down";
		self.but_movedown.action = {
			var pos, newpos, item;
			pos = self.selectedlistview.value;
			item = selectedlist.removeAt(pos);
			selectedlist.insert((pos+1).clip(0,selectedlist.size), item);
			self.selectedlistview.value = (pos+1).clip(0,selectedlist.size-1);
			self.selectedlistview.items = selectedlist.collect(_.label);
		};

		self.but_moveup = Button.new(self.buttons_layout, Rect(0,0,50,20));
		self.but_moveup.string = "Up";
		self.but_moveup.action = {
			var pos, newpos, item;
			pos = self.selectedlistview.value;
			item = selectedlist.removeAt(pos);
			selectedlist.insert((pos-1).clip(0,selectedlist.size), item);
			self.selectedlistview.value = (pos-1).clip(0,selectedlist.size-1);
			self.selectedlistview.items = selectedlist.collect(_.label);
		};

		self.selectedlistview = ListView.new(self.right_layout, Rect(0,0,300,150));
		self.selectedlistview.items = selectedlist.collect(_.label);
		self.selectedlistview.action = { arg view, b, c;
			[a, b, c].debug("action");
			self.display_file(selectedlist[self.selectedlistview.value]);
		};
		
		"con7".debug;
		self.plotter = Plotter("plot", parent: self.right_layout);

		self.but_apply = Button.new(self.right_layout, Rect(0,0,80,20));
		self.but_apply.string = "Apply";
		self.but_apply.action = {
			if((selectedlist.size < 1) and: {self.displayed_file.notNil}) {
				selectedlist = [self.displayed_file];
			} {
				// noop
			};
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
		self.librarylistview.value = 0;
		"con8".debug;
		self.librarylistview.debug("libview");
		self.librarylistview.action.value(self.librarylistview);
		"con9".debug;

		self.window.front;

		self;
	},

	display_file: { arg self, file;
		var sf, sig;
		self.displayed_file = file;
		file.load_in_signal(self.signal);
		self.plotter.value = self.signal.as(Array);
		self.window.refresh;
		if(self.synthnode.notNil) {
			~load_signal_in_wavetable_buffer.(self.buffer, self.signal);
		};
	}

);

~class_load_wavetable_dialog_old = (
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

