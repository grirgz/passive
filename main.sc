
~load_passive = {

	///////////////// change to the directory where files are located /////////////////

	~current_dir = "~/code/sc/passive/";

	//////////////////////////////////////////////

	"loading config.sc".debug;
	(~current_dir +/+ "config.sc").standardizePath.load;
	"loading synth.sc".debug;
	(~current_dir +/+ "synth.sc").standardizePath.load;
	"loading wavetable.sc".debug;
	(~current_dir +/+ "wavetable.sc").standardizePath.load;
	"loading ui.sc".debug;
	(~current_dir +/+ "ui.sc").standardizePath.load;
	"loading paramdata.sc".debug;
	(~current_dir +/+ "paramdata.sc").standardizePath.load;
	"loading control.sc".debug;
	(~current_dir +/+ "control.sc").standardizePath.load;
};

~new_passive = {
	if(~passive.notNil) {~passive.destructor };
	~passive = ~class_passive_controller.new;
};
