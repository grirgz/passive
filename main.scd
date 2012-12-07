
~load_passive = {

	///////////////// change to the directory where files are located /////////////////

	~current_dir = "~/code/sc/passive/";

	//////////////////////////////////////////////

	"loading config.scd".debug;
	(~current_dir +/+ "config.scd").standardizePath.load;
	"loading synth.scd".debug;
	(~current_dir +/+ "synth.scd").standardizePath.load;
	"loading wavetable.scd".debug;
	(~current_dir +/+ "wavetable.scd").standardizePath.load;
	"loading ui.sc".debug;
	(~current_dir +/+ "ui.scd").standardizePath.load;
	"loading paramdata.sc".debug;
	(~current_dir +/+ "paramdata.scd").standardizePath.load;
	"loading control.sc".debug;
	(~current_dir +/+ "control.scd").standardizePath.load;
};

~load_passive.(); // load code files

// this function destroy the old session when you start a new one, but you can start multiples sessions if you want
~new_passive = {
	if(~passive.notNil) {~passive.destructor };
	~passive = ~class_passive_controller.new;
};
