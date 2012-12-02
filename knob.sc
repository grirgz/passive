
0.2.clip(0,00.1)

(
~myclip = { arg val, mi, ma;
	if(val < mi) {
		val = mi;
	} {
		if(val > ma) {
			val = ma;
		}
	};
	val;
};

w=Window.new;
v=UserView(w, w.view.bounds.insetBy(160,160));
v.resize = 5;
v.background_(Color.white);
~rotate = 0;
v.drawFunc={|uview|
	var bounds = uview.bounds;
	var start_angle = (5/8*pi);
	var angle;
	var width = 0.5;
	var width2 = 0.3;
	var width3 = 0.1;
	var pos, midi_pos;
	var midi_angle;
	var len, len_rad;
	var len2;
	var len3;
	var end_angle;
	var rayon = [2.3, 2.6, 3.0];
	var full_len; 
	var move_angle = 14/8*pi;
	var ilen2;
	pos = ~rotate % 1;
	midi_pos = 0.2;

	//start_angle = 0;

	//Pen.moveTo(uview.bounds.left@uview.bounds.top);
	//Pen.strokeOval(Rect(1/6*bounds.width,1/6*bounds.height,uview.bounds.width/2,uview.bounds.height/2));
	angle = ((pos*move_angle)+start_angle) % 2pi;
	midi_angle = ((midi_pos*move_angle)+start_angle) % 2pi;
	//len = (width * 14/8*pi).clip2((14/8*2*pi)-angle);

	len = width.clip2(1 - pos) * move_angle;
	len2 = width2.clip2(1 - pos) * move_angle;
	ilen2 = ~myclip.(width2, 0, pos) * move_angle;
	len3 = width3.clip2(1 - pos) * move_angle;
	full_len = move_angle;
	//end_angle = ((1*2pi)+start_angle) % 2pi
	[width, len, len_rad, pos, angle, 1 - pos].debug("width, len, len_rad, pos, angle, 1 - pos");
	bounds = (0@0) @ bounds.extent;
	bounds.postln;
	//Pen.moveTo(bounds.extent/2);
	//Pen.strokeOval(bounds.insetBy(20,20));
	//Pen.rotate(~rotate * 2pi, bounds.extent.x/2, bounds.extent.y/2);
	Pen.color = Color.black;
	//Pen.addArc(bounds.extent/2, 120, angle+(width*2pi), angle-(width*2pi));
	Pen.addArc(bounds.extent/2, bounds.width/4.0, 0,2pi);
	Pen.stroke;
	Pen.addAnnularWedge(bounds.extent/2, bounds.width/9.0, bounds.width/4.0, angle,0.1);
	Pen.stroke;
	Pen.color = Color.blue(0.9);
	Pen.addAnnularWedge(bounds.extent/2, bounds.width/5.0, bounds.width/4.0, midi_angle,0.1);
	Pen.stroke;
	Pen.width = 2;


	Pen.color = Color.gray(0.8);
	rayon.do { arg ray;
		Pen.addArc(bounds.extent/2, bounds.width/ray, start_angle,full_len);
		Pen.stroke;
	};

	Pen.color = Color.green.alpha_(0.5);
	Pen.addArc(bounds.extent/2, bounds.width/3.0, angle,len);
	Pen.stroke;
	if(width > (1-pos)) {
		Pen.addArc(bounds.extent/2, bounds.width/3.0, move_angle+start_angle+0.1,0.1);
	};
	Pen.stroke;

	Pen.color = Color.red;
	Pen.addArc(bounds.extent/2, bounds.width/2.6, angle,len2);
	Pen.stroke;
	Pen.color = Color.red(0.5);
	Pen.addArc(bounds.extent/2, bounds.width/2.6, angle,0-ilen2);
	Pen.stroke;

	Pen.color = Color.blue;
	Pen.addArc(bounds.extent/2, bounds.width/2.3, angle,len3);
	Pen.stroke;
};
v.mouseDownAction={ arg view, x, y;
	~x = x;
	~irotate = ~rotate;
	"down".postln;
	//~rotate = ~rotate+0.1; v.refresh
};
v.mouseMoveAction = { arg view, x, y;
	var ro, nx;
	[x, x - ~x, ~x, ro].debug("x, x-~x, ~x");
	nx = x - ~x;
	ro = ~myclip.( (nx/100) + ~irotate, 0, 0.999 );
	
	~rotate = ro;
	view.refresh;
};
w.front;
)
pi

sign(-1)

0.3.clip2(0.1,0.4)

(
w = Window.new.front;
w.view.background_(Color.white);
w.drawFunc = {
    Pen.translate(100, 100);
    1000.do{
        // set the Color
        Pen.color = Color.green(rrand(0.0, 1), rrand(0.0, 0.5));
        Pen.addAnnularWedge(
            (100.rand)@(100.rand),
            rrand(10, 50),
            rrand(51, 100),
            2pi.rand,
            2pi.rand
        );
        Pen.perform([\stroke, \fill].choose);
    }
};
w.refresh;
)
nil.clip2



(
s.waitForBoot {
        w=Window.new;
		h = HLayoutView(w, w.view.bounds);
		w.front;
        v=ModKnob.new(h, Rect(10,20,170,70));
		v.keyDownAction = {
			"plop".postln;

		};
		v.action = { "plop".postln; };
		v.width1 = 0.5;
		v.polarity1 = \bipolar;
        v=ModKnob.new(h, Rect(10,20,70,70));
		v.width1 = -0.5;
		v.width2 = 0.1;
		v.width3 = 1.1;
		v.value = 0.6;
        v=ModKnob.new(h, Rect(10,20,70,70));
		v.width1 = 0.5;
}
)
(
//        GUI.qt;
        w = Window.new.front;
        v = MyWidget( w, Rect( 10, 10, 300, 300) );
        w.front;
)


(
~myclip = { arg val, mi, ma;
	if(val < mi) {
		val = mi;
	} {
		if(val > ma) {
			val = ma;
		}
	};
	val;
};

w=Window.new;
v=UserView(w, w.view.bounds.insetBy(100,100));
v.resize = 5;
v.background_(Color.white);
~rotate = 0;
v.drawFunc={|uview|
	var bounds = uview.bounds;
	bounds = (0@0) @ bounds.extent;
	Pen.color = Color.red;
	Pen.addRect(bounds.insetBy(10,10));
	Pen.stroke;
	Pen.stringInRect("W", bounds.insetBy(30,30));
	Pen.stroke;
};
v.mouseDownAction={ arg view, x, y;
	~x = x;
	~irotate = ~rotate;
	"down".postln;
	//~rotate = ~rotate+0.1; v.refresh
};
v.mouseMoveAction = { arg view, x, y;
	var ro, nx;
	[x, x - ~x, ~x, ro].debug("x, x-~x, ~x");
	nx = x - ~x;
	ro = ~myclip.( (nx/100) + ~irotate, 0, 0.999 );
	
	~rotate = ro;
	view.refresh;
};
w.front;
)


(
w = Window.new;
v = StaticText.new(w, Rect(10,10,15,15));
v.background = Color.white;
v.string = "W";
v.mouseDownAction={ arg view, x, y;
	"down".postln;
};
v.mouseMoveAction = { arg view, x, y;
	"move".postln;
};
w.front;
)

(
w = Window.new;
v = PopUpMenu.new(w, Rect(10,10,80,20));
v.items = ["bla", "rah"];
v.background = Color.white;
w.front;
)

(
~myclip = { arg val, mi, ma;
	if(val < mi) {
		val = mi;
	} {
		if(val > ma) {
			val = ma;
		}
	};
	val;
};

w=Window.new;
v=UserView(w, w.view.bounds.insetBy(180,100));
v.resize = 5;
//v.background_(Color.white);
~rotate = 0;
v.drawFunc={|uview|
	var bounds = uview.bounds;
	var pos = 0.9;
	var draw_band;
	var start, end, len;
	bounds = (0@0) @ bounds.extent;
	start = 10;
	end = bounds.height-10;
	len = end-start;
	pos = ~doubleclip.(pos, 0, 1);



	draw_band = { arg offset, range, polarity, color;
		var nstart;
		var nend;

		range = range.clip2(1);
		Pen.width = 2;

		if(polarity == \bipolar) {
			range = range/2;
		};

		nstart = (start+((1-(pos+range))*len));
		nend = (end-(pos*len));

		if( nstart < start) {
			Pen.color = color;
			nstart = start;
			Pen.line(offset@(start-5), offset@(start-2));
			Pen.stroke;
		};
		if( nstart > end ) {
			Pen.color = color;
			nstart = end;
			Pen.line(offset@(end+5), offset@(end+2));
			Pen.stroke;

		};
		
		Pen.color = Color.gray(0.8);
		Pen.line(offset@start, offset@end);
		Pen.stroke;

		Pen.color = color;
		[bounds.width, len, start, end, ((pos-range)*len), start+((pos-range)*len)].debug;
		Pen.line(offset@nend, offset@nstart);
		Pen.stroke;

		if(polarity == \bipolar) {
			range = 0-range;
			nstart = (start+((1-(pos+range))*len));
			nend = (end-(pos*len));

			if( nstart < start) {
				Pen.color = color;
				nstart = start;
				Pen.line(offset@(start-5), offset@(start-2));
				Pen.stroke;
			};
			if( nstart > end ) {
				Pen.color = color;
				nstart = end;
				Pen.line(offset@(end+5), offset@(end+2));
				Pen.stroke;

			};

			Pen.color = color.copy.alpha_(0.5);

			Pen.line(offset@nend, offset@nstart);
			Pen.stroke;
		};


	};

	draw_band.(10, -0.4, \bipolar, Color.red);
	draw_band.(15, 0.1, \unipolar, Color.blue);
	draw_band.(20, 0.1, \unipolar, Color.green);
	


};
w.front;
)

(
w = Window.new;
v = ModSlider.new(w, Rect(10,10,40,150));
v.range1 = -0.5;
v.range2 = 0.5;
v.range3 = nil;
v.polarity2 = \bipolar;
//v.background = Color.white;
w.front;
)


//~class_filter_view = (
	//	new: { arg self, parent, controllers;
	//		// controller_names: [mute, filter_kind, arg1, arg2, arg3, amp]
	//		var frame_size;
	//		self = self.deepCopy;
	//		frame_size = 080@155;
	//
	//		"blobl 1".debug;
	//		self.vlayout = VLayoutView.new(parent, Rect(0,0,frame_size.x,frame_size.y));
	//		self.header_layout = HLayoutView.new(self.vlayout, Rect(0,0,frame_size.x,20));
	//
	//		"blobl 2".debug;
	//		self.name = ~class_mute_label_view.new(self.header_layout, controllers[0]);
	//		"blobl 2.1".debug;
	//		self.popup = ~class_popup_view.new(self.header_layout, controllers[1]);
	//
	//		"blobl 2.2".debug;
	//		self.body_layout = HLayoutView.new(self.vlayout, Rect(0,0,frame_size.x,130));
	//		self.body_layout.background = Color.gray(0.5);
	//		"blobl 3".debug;
	//
	//		self.knob1 = ~class_pknob_view.new(self.body_layout, controllers[2]);
	//		self.knob2 = ~class_pknob_view.new(self.body_layout, controllers[3]);
	//		self.knob3 = ~class_pknob_view.new(self.body_layout, controllers[4]);
	//		self.fader_knob = ~class_simple_slider_view.new(self.body_layout, controllers[5]);
	//		"blobl 4".debug;
	//
	//		self;
	//	}
//);


//~class_osc_view = (
	//	new: { arg self, parent, main_controller, controllers;
	//		// controller_names: [mute, wt, pitch, wtpos, intensity, amp, fader]
	//		var frame_size;
	//		self = self.deepCopy;
	//		frame_size = 080@155;
	//
	//		self.vlayout = VLayoutView.new(parent, Rect(0,0,frame_size.x,frame_size.y));
	//		self.header_layout = HLayoutView.new(self.vlayout, Rect(0,0,frame_size.x,20));
	//
	//		self.name = ~class_mute_label_view.new(self.header_layout, controllers[0]);
	//		self.wavetable_popup = ~class_popup_view.new(self.header_layout, controllers[1]);
	//
	//		self.body_layout = HLayoutView.new(self.vlayout, Rect(0,0,frame_size.x,130));
	//		self.body_layout.background = Color.gray(0.5);
	//
	//		self.pitch_knob = ~class_pknob_view.new(self.body_layout, controllers[2]);
	//		self.wt_knob = ~class_pknob_view.new(self.body_layout, controllers[3]);
	//		self.intensity_knob = ~class_pknob_view.new(self.body_layout, controllers[4]);
	//		self.amp_knob = ~class_pknob_view.new(self.body_layout, controllers[5]);
	//		self.fader_knob = ~class_simple_slider_view.new(self.body_layout, controllers[6]);
	//
	//		self;
	//	}
	//
//);

//~class_feedback_view = (
	//	new: { arg self, parent, controllers;
	//		// controller_names: [mute, amp, fader]
	//		var frame_size;
	//		self = self.deepCopy;
	//		frame_size = 080@155;
	//
	//		~class_frame_view[\make_frame].(self, parent, frame_size, controllers[0], nil, controllers[2]);
	//		self.knob1 = ~class_pknob_view.new(self.body_layout, controllers[1]);
	//
	//		self;
	//	}
	//
//);




(
var size;
size = 16;
w = Window.new;
w.view.decorator = FlowLayout(w.view.bounds);
m = MultiSliderView(w, Rect(0, 0, size*20, 100));   
m.value_(Array.fill(size, {0.01}));
m.isFilled_(true); // width in pixels of each stick
m.drawRects = true;
m.indexThumbSize_(20); // spacing on the value axis
m.showIndex = true;
m.fillColor = Color.gray(0.5);
m.gap_(0);
w.front;
)

// rotate the above graph
(
m.bounds_(Rect(0, 0, 100, 350));
m.indexIsHorizontal_(false);
)



(
n=40;

w = Window("MultiSlider Options", Rect(200, Window.screenBounds.height-550, 600, 450));
f={ 
    w.view.decorator = FlowLayout( w.view.bounds, 10@10, 10@2 );
    m = MultiSliderView(w,Rect(0,0,580,200)); // default thumbWidth is 13
    m.value=Array.fill(n, {|v| 0.5+((0.3*v).sin*0.25)});
    m.action = { arg q;q.value.postln; };

    StaticText(w,380@18).string_("indexThumbSize or thumbSize");
    Slider(w,580@10).action_({arg sl; m.indexThumbSize=sl.value*24}).value_(0.5);
    StaticText(w,380@18).string_("valueThumbSize");
    Slider(w,580@10).action_({arg sl; m.valueThumbSize=sl.value*24}).value_(0.5);
    StaticText(w,580@18).string_("xOffset or gap");
    Slider(w,580@10).action_({arg sl; m.xOffset=sl.value*50});
    StaticText(w,580@18).string_("startIndex");
    Slider(w,580@10).action_({arg sl; m.startIndex = sl.value *m.size};);

    CompositeView(w,580@10);//spacer
    Button(w,100@20).states_([["RESET",Color.red]])
        .action_({ w.view.removeAll; f.value; });
    h=StaticText(w,450@18).string_("").stringColor_(Color.yellow);
    Button(w,100@20).states_([["elasticMode = 0"],["elasticMode = 1",Color.white]])
        .action_({|b| m.elasticMode = b.value});
    Button(w,160@20).states_([["indexIsHorizontal = false"],["indexIsHorizontal = true",Color.white]])
        .action_({|b| m.indexIsHorizontal = b.value.booleanValue}).value_(1);
    Button(w,120@20).states_([["isFilled = false"],["isFilled = true",Color.white]])
        .action_({|b| m.isFilled = b.value.booleanValue});
    Button(w,120@20).states_([["drawRects = false"],["drawRects = true",Color.white]])
        .action_({|b| m.drawRects = b.value.booleanValue}).valueAction_(1);
    Button(w,100@20).states_([["drawLines = false"],["drawLines = true",Color.white]])
        .action_({|b| m.drawLines = b.value.booleanValue});
    Button(w,160@20).states_([["readOnly = false"],["readOnly = true",Color.white]])
        .action_({|b| m.readOnly = b.value.booleanValue});
    Button(w,120@20).states_([["showIndex = false"],["showIndex = true",Color.white]])
        .action_({|b| m.showIndex = b.value.booleanValue});
    Button(w,120@20).states_([["reference = nil"],["reference filled",Color.white],["reference random",Color.yellow]])
        .action_({|b| b.value.booleanValue.if({
            (b.value>1).if(
                {m.reference=Array.fill(n, {1.0.rand})},
                {m.reference=Array.fill(m.size, {0.5})});
                },{ q=m.value;m.reference=[]; h.string="reference can't be returned to nil presently. please hit RESET."}
            )
        });
    Button(w,180@20).states_([["fillColor = Color.rand"]]).action_({m.fillColor=Color.rand});
    Button(w,180@20).states_([["strokeColor = Color.rand"]]).action_({m.strokeColor=Color.rand});
    Button(w,180@20).states_([["background = Color.rand"]]).action_({m.background=Color.rand});

};
f.value;
w.front;

)



(
// press shift to extend the selection
// use as waveView: scrubbing over the view returns index
// if showIndex(false) the view is not refreshed (faster);
// otherwise you can make a selection with shift - drag.
var size, file, maxval, minval;
size = 640;
a = Window("test", Rect(200 , 140, 650, 150));
a.view.decorator = FlowLayout(a.view.bounds);
b = MultiSliderView(a, Rect(0, 0, size, 50));
b.readOnly_(true);
a.view.decorator.nextLine;

d = Array.new;
c = FloatArray.newClear(65493);

r = Slider( a, Rect(0, 0, size, 12));
r.action = {arg ex; b.gap = (ex.value * 4) + 1};

file = SoundFile.new;
file.openRead("sounds/a11wlk01.wav");
file.numFrames.postln;
file.readData(c);
// file.inspect;
file.close;
minval = 0;
maxval = 0;
f = Array.new;
d = Array.new;
c.do({arg fi, i;
    if(fi < minval, {minval = fi});
    if(fi > maxval, {maxval = fi});

    //f.postln;
    if(i % 256 == 0,{
        d = d.add((1 + maxval ) * 0.5 );
        f = f.add((1 + minval ) * 0.5 );

        minval = 0;
        maxval = 0;
    });
});

b.reference_(d); // this is used to draw the upper part of the table
b.value_(f);

r = Slider( a, Rect(0, 0, size, 12));
r.action = {arg ex; b.startIndex = ex.value *f.size};

// b.enabled_(false);
b.action = {arg xb; ("index: " ++ xb.index).postln};
b.drawLines_(true);
b.drawRects_(false);
b.isFilled_(true);
b.selectionSize_(10);
b.index_(10);
b.thumbSize_(1);
b.gap_(0);
b.colors_(Color.black, Color.blue(1.0,1.0));
b.showIndex_(true);
a.front;

)



(
w = Window.new("plop", Rect(0,0,600,400));
~st = ~class_stepper_view.new(w);
w.front;
)
~st.multislider.value



(


w = Window.new("Testing BoxGrid", Rect(10, 500, 840, 142)).front;

a = BoxGrid.new(w, bounds: Rect(20, 20, 760, 20), columns: 16, rows: 1);

a.setTrailDrag_(true, true);
a.setNodeBorder_(2);


)





(

w = Window("setDeviation", Rect(300, 300, 300, 150));
a = RangeSlider(w, Rect(10, 10, 200, 30))
    .lo_(0)
    .hi_(1);
b = Slider(w, Rect(10, 50, 200, 30))
    .action_(
        {   arg me;
            a.setDeviation(c.value, b.value);
        });
c = Slider(w, Rect(10, 100, 200, 30))
    .action_(
        {   arg me;
            a.setDeviation(c.value, b.value);
        }
    );
c.valueAction = 0.2;
a.step = 0.2;
w.front;
)

(
)

(
w = Window.new;
~class_curvegraph_view.new(w, 16, 600@300, ~curvebank);
w.front;

)

(
~myclip = { arg val, mi, ma;
	if(val < mi) {
		val = mi;
	} {
		if(val > ma) {
			val = ma;
		}
	};
	val;
};

w=Window.new;
v=UserView(w, Rect(0,0,200,200));
v.resize = 5;
//v.background_(Color.white);
~rotate = 0;
~numstep = 5;
~ampmod = 1 ! ~numstep;
v.mouseMoveAction = { arg view, x, y;
	var nx, ny, ox, oy;
	ny = 1-(y/v.bounds.height);
	nx = (x/v.bounds.width).trunc(1 / ~numstep)* ~numstep;
	ox = ~myclip.( nx, 0, ~numstep );
	oy = ~myclip.( ny, 0, 0.999 );
	[x,y, nx, ny, ox, oy].debug("x,y, nx, ny, ox, oy");
	
	~ampmod[ox] = oy;
	view.refresh;
};

v.mouseDownAction = v.mouseMoveAction;

~curvebank = (

	saw4: { arg x;
		var y;
		y = sin(sqrt(x)/(x*2)).linlin(0,1,0,1);
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
~curve = [\sin4, \sin2, \line1, \negline1, \sin1];
~curve = \saw4 ! 4;

v.drawFunc={|uview|
	var bounds = Rect(0,0,uview.bounds.width,uview.bounds.height);
	var draw_band;
	var size = bounds.width @ bounds.height;
	var numstep = ~numstep;
	var ampmod = ~ampmod;
	//"--------------begin draw".debug;

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
		draw_band.(offset*(size.x/numstep).trunc, (size.x/numstep).trunc @ size.y, ampmod.wrapAt(i), 0, ~curvebank[~curve.wrapAt(i)]);
	};
	Pen.stroke;

	Pen.color = Color.blue;

	numstep.do { arg offset;
		Pen.lineDash = [0.5,1]*5;
		//"-------begin sep".debug;
		offset = (offset+1)*(size.x/numstep);
		Pen.line(offset@0, offset@size.y);
		Pen.stroke;
	};
	


};
w.front;
)



(
var parent;
parent = Window.new;
~class_modmatrix_view.new(parent);
parent.front;
)



(
s.waitForBoot({    // only needed if you are using sound
    w = Window.new.front;

    // store various kinds of objects in the drag source

    // a string source
    a = DragSource(w, Rect(10, 10, 150, 20)).align_(\center);
    a.object = "I am a string source";

    // a Float source
    b = DragSource(w, Rect(10, 40, 150, 20)).align_(\center);
    b.object = 2.234;

    // a Point source
    c = DragSource(w, Rect(10, 70, 150, 20)).align_(\center);
    c.object = Point(20, 30);

    // A sound function source
    // dragLabel_() is used for the label while dragging
    d = DragSource(w, Rect(10, 100, 150, 20)).align_(\center);
    d.object = { Synth(\default) };
    d.dragLabel = " I am a sound function.\n My dragLabel_() is set \n to inform you about that ";

    // A sound function source
    // here the string label is independent of the content type (Function)
    // dragLabel_() is used for the label while dragging
    f = DragSource(w, Rect(10, 130, 150, 20)).align_(\center).setBoth_(false);
    f.object = { { SinOsc.ar(440,0,0.4) }.play };
    f.string = "My label is independent";
    f.dragLabel = " My dragLabel_() says \n I am dragging a sound function ";

    // receive anthing
    g = DragSink(w, Rect(170, 10, 200, 20)).align_(\center);
    g.string = "recieve anything, do nothing";

    // receive only floats
    g = DragSink(w, Rect(170, 40, 200, 20)).align_(\center);
    g.string = "I only like floats";
    g.canReceiveDragHandler = { View.currentDrag.isFloat };

    // receive only numbers and points, and convert them to rects
    h = DragSink(w, Rect(170, 70, 200, 20)).align_(\center);
    h.string = "I convert to Rect";
    h.canReceiveDragHandler = { View.currentDrag.isKindOf(Number) || View.currentDrag.isKindOf(Point) };
    h.receiveDragHandler = { arg v; h.object = View.currentDrag.asRect };

    // receive only functions, and try to play them
    i = DragSink(w, Rect(170, 100, 200, 20)).align_(\center);
    i.string = "I evaluate a (sound) function";
    i.canReceiveDragHandler = { View.currentDrag.isKindOf(Function) };
    i.receiveDragHandler = { arg v;
        i.object = View.currentDrag.value;
        i.string = "click here for silence";
        i.background_(Color.red)};
    i.mouseDownAction_({
        i.object.free;
        i.string = "I evaluate a (sound) function";
        i.background_(Color.clear) });

    StaticText(w, Rect(10, 200, 380, 50))
        .stringColor_(Color.white)
        .string_("Try dragging any item on the left -----> to any item on the right");
});
)



(
	w = Window.new;
    f = DragSource(w, Rect(10, 130, 150, 20)).align_(\center).setBoth_(false);
	g = StaticText.new(w, Rect(10,10,50,10));
	g.background = Color.white;
	g.string ="rahhh";
	g.canReceiveDragHandler = { true };
	w.front;
)













(

~load_curve_in_array = { arg size, curvefunc;
	FloatArray.fill(size, { arg i;
		curvefunc.(i/size)
	})
};

)

~load_curve_in_array.(512, ~curvebank[\sin1]);
~curvebank[\sin1].(1)
(

~load_curve_in_buffer = { arg buffer, curvefunc;
	var size = buffer.numFrames;
	size.debug("size");
	buffer.debug("buffer");
	buffer.loadCollection(FloatArray.fill(size, { arg i;
		curvefunc.(i/size)
	}).debug("Array"),0, { "done".debug; })
};

)

b = Buffer.alloc(s, 512, 1)
b.get(0, {|msg| msg.postln});
~load_curve_in_buffer.(b, ~curvebank[\sin1]);
b
b.plot
b.index


(
var size = 512;
b = Buffer.alloc(s,size);
b.loadCollection(FloatArray.fill(size, { arg x; 
		sin(x/size*2pi);
}));
)

b.free;
b.get(1, {|msg| msg.postln});
(
{
	var buf = b;
	var sig, sig2;
	var freq = 500;
	var fm = 1;
	var scale = BufFrames.kr(buf)/ControlRate.ir;
	sig2 = SinOsc.kr(fm);
	sig = BufRd.kr(1, buf.bufnum, Phasor.kr(0, fm*scale, 0, BufFrames.kr(buf)));
	//sig.poll;
	//SinOsc.ar(freq + (sig * 100))
	[sig, sig2]

}.plot()
)
BufRead
s.boot
BufRd
{ ControlRate.ir.poll  }.play;

(
var size = 512;
b = Buffer.alloc(s,size);
b.loadCollection(FloatArray.fill(size, { arg x; 
		sin(x/size*2pi);
}));
)

b.free;
b.get(1, {|msg| msg.postln});
(
{
	var buf = b;
	var sig, sig2;
	var freq = 500;
	var fm = 1;
	var scale = BufFrames.kr(buf)/ControlRate.ir;
	sig2 = SinOsc.kr(fm);
	sig = PlayBuf.kr(1, buf.bufnum, Phasor.kr(0, fm*scale, 0, BufFrames.kr(buf)));
	//sig.poll;
	//SinOsc.ar(freq + (sig * 100))
	[sig, sig2]

}.plot()
)

(
{
SinOsc.ar(500) ! 2;
}.play

)




(
n=20;
w = Window.new.front;
m = MultiSliderView(w,Rect(10,10,n*13+2,100)); //default thumbWidth is 13
m.value=Array.fill(n, {|v| v*0.05}); // size is set automatically when you set the value
m.action = { arg q;
    q.value.postln;
};
)




(

a = Patch({
        SinOsc.ar(800,0.0)
});


c = Bus.audio;
a.play(bus: c);
// a is now playing on bus c, which we can't hear

// patch b will listen to buss c and play one enveloped grain
b = Patch({ arg tone;
        var gate;
        gate = Trig1.kr(1.0,0.25);
        tone = In.ar(tone,1);
        tone * EnvGen.kr(Env([0,1,0],[0.05,0.05],\welch,1),gate,doneAction: 2)
},[
        c.index
]);

b.prepareForPlay(s);

// play one grain
b.spawn(atTime: 0.1);

// play 100 grains
Routine({
        1.0.wait;
        100.do({
                b.spawn(atTime: 0.1);
                0.25.wait
        })
}).play(SystemClock)

)
)

Patch(\p_oscillator, (freq: 800)).play


b = Buffer.alloc(s, 512, 1);
b.sine1([1.0,2,3], true, true, true);
b.plot

(
s = Signal.newClear(512/2);
s.waveFill({ arg x, i; sin(x).max(0) }, 0, 3pi);
s.waveFill({ arg x, i; if(x>pi, 0, 1) }, 0, 2pi);
s.plot;
)
w = b.loadCollection(s.asWavetable);

(
Pdef(\plop, Pbind(
	\instrument, \oscillator,
	\degree, Pseq([0],inf),
	\wt, b.bufnum,
	\dur, 1,
	\amp, 0.1
)).play;
);

(
Pdef(\plop, Pbind(
	\instrument, \massive,
	\degree, Pseq([0,2,4],inf),
	\osc1_wt, b.bufnum,
	\osc2_wt, b.bufnum,
	\osc3_wt, b.bufnum,
	\filter2_arg1, Pkey(\freq)*8,
	\filter2_arg2, 0.1,
	\filter_mix, 1.00,
	\filter_parseq, 0,
	\dur, 1,
	\amp, 0.5
)).play;
);

(
8.do({ arg i;
	var n, a;
	// allocate table
	s.sendMsg(\b_alloc, i, 1024);
	// generate array of harmonic amplitudes
	n = (i+1)**2;
	a = Array.fill(n, { arg j; ((n-j)/n).squared.round(0.001) });
	// fill table
	s.performList(\sendMsg, \b_gen, i, \sine1, 7, a);
});
)

Patch(a).play
a = Instr(\simple).gui
Instr(\simple).prepareForPlay
a = Patch(\simple, (fm:4))
a = Patch(\simple, [0.1, 400, 4])
a.freq = 400
a.fm = 2
a.args
a.play
Patch(a, (freq:100))

a = Patch(\simple, (fm:b.asMap))
a.play

b = Bus.control(s, 1)
b.set(8)
a = Instr(\simple).asSynthDef((fm:b))
a.add
c = Synth.new(a.name, [\freq, 405])
Patch(\simple).play
a.name
b.free

(
Instr(\simple, { arg amp=0.1, freq, fm, gate=1;
	var ou, sig;
	sig = SinOsc.ar(fm);
	ou = SinOsc.ar(freq+(sig*0.5*freq));
	ou = ou * EnvGen.ar(Env.adsr(0.1,0.1,1,0.1),gate,doneAction:2);
	ou = ou * amp;
}, [\amp, \freq, \freq]);
)



(
~sy = SynthDef(\Pulse,{ arg freq=440.0,width=0.5,mul=0.1;
        Pulse.ar( freq, width, mul )
});

)

~sy.add
~sy.allControlNames
SynthDef


(
Instr(\Pulse,{ arg freq=440.0,width=0.5,mul=0.1;
        Pulse.ar( freq, width, mul )
}, [\freq.asSpec, \freq.asSpec, \freq.asSpec]);

)

p = Patch( \Pulse, [ 500, 0.2, 0.1 ]);
~sy = Patch( \Pulse, [ 500, 0.2, 0.1 ]).asSynthDef;
~sy.add
~sy.allControlNames
~sy.gui
p.synthDef
p.play

//the default server will be booted, the SynthDef written and loaded
p.play;

)

p.stop;




(
// given a simple instrument
Instr(\Pulse,{ arg steps, freq=440.0,width=0.5,mul=0.1, gate=1;
	var ou;
	ou = Pulse.ar( freq, width, mul ) * EnvGen.ar(Env.linen(0.1,0.5,0.1),gate,doneAction:2);

	ou = ou * EnvGen.ar(Env.linen(0.1,0.5,0.5,1),gate,doneAction:2);
}, [NonControlSpec()]);
)

p = Patch( \Pulse, [ 200, 0.2, 0.1 ]).play;
p = Patch( \Pulse, (freq:200, gate:1, steps:~args[\steps])).play;
~args[\steps]

(
p = Patch( \passive, (enabled:0, kinds:0, mod:0, msteps:0, routing:0, freq:200, gate:1)).play;


)




b = Bus.control(s, 1)
b.set(8)


a = Patch(\simple, (fm:b.asMap))
a.play

a = Patch(\simple, (fm:b))
a.play

a = Instr(\simple).asSynthDef((fm:b))
a.add
c = Synth.new(a.name, [\freq, 405])


(
Instr(\simple, { arg amp=0.1, freq, fm, gate=1;
	var ou, sig;
	sig = SinOsc.ar(fm);
	ou = SinOsc.ar(freq+(sig*0.5*freq));
	ou = ou * EnvGen.ar(Env.adsr(0.1,0.1,1,0.1),gate,doneAction:2);
	ou = ou * amp;
}, [\amp, \freq, \freq]);
)

(
n=20;
w = Window.new.front;
m = NumberBox(w,Rect(10,10,n*13+2,100)); //default thumbWidth is 13
)

(
        w=Window.new;
		h = HLayoutView(w, w.view.bounds);
		w.front;
        v=ModKnob.new(h, Rect(10,20,170,70));
)




(
Instr(\osc, { arg curve, amp=0.1, gate=1, pan=0, freq=200;
	var ou;
	ou = Osc.ar(curve, freq);
	ou = ou * EnvGen.ar(\adsr.kr(Env.adsr(0.01,0.1,0.8,0.1)),gate,doneAction:2);
	ou = Pan2.ar(ou, pan, amp);
});
)


(
p = Patch(\osc, (curve:~buf)).play
p.synthDef
)
{SinOsc.ar}.play

	{ SinOsc.ar(400+Osc.ar(~buf.bufnum, 0.2,mul:100)); }.play
	~buf.bufnum

~wavetable_s = Signal.newClear(512);
~wavetable_s.waveFill(~curvebank[\sin1], 0,1);
~wavetable = ~wavetable_s.asWavetable;
~wavetable.size
~buf = Buffer.alloc(s, 4096);
~buf.loadCollection(~wavetable)
~buf
~buf.read(f)

'kjkj'.standardizePath
'/home/ggz/Musique/Cosine Positive.wav'.standardizePath

{SinOsc.ar}.play
x = { Osc.ar(~buf, MouseX.kr(20, 680)) ! 2  }.play;
x = { Osc.ar(~buf, MouseX.kr(20, 680) * [1.001,0.999]) ! 2  }.play;
x = { Osc.ar(~buf, MouseX.kr(20, 680) * [1,1+SinOsc.kr(0.1,mul:0.1)]) ! 2  }.play;
f = "/home/ggz/Musique/archwavetable/Architecture Waveforms 2010 Wav24/Architecture Waveforms 2010 Wav24/Modulo - FM/ModFM 001 002.wav" 
f = "/home/ggz/Musique/archwavetable/Architecture Waveforms 2010 Wav24/Architecture Waveforms 2010 Wav24/Modulo - FM/ModFM 004 128.wav" 
f = "/home/ggz/Musique/archwavetable/Architecture Waveforms 2010 Wav24/Architecture Waveforms 2010 Wav24/Misc - Artificial/MWave Fish Hook.wav"
f = "/home/ggz/Musique/archwavetable/Architecture Waveforms 2010 Wav24/Architecture Waveforms 2010 Wav24/Misc - Artificial/Broken Bits.wav"
f = "/home/ggz/Musique/archwavetable/Architecture Waveforms 2010 Wav24/Architecture Waveforms 2010 Wav24/Spectral - SPM-Log/SPM-Log 01 01 01 01.wav" 
f = "/home/ggz/Musique/archwavetable/Architecture Waveforms 2010 Wav24/Architecture Waveforms 2010 Wav24/Misc - Assymetrical/Asymmetrical 011.wav" 
~load_sample_in_wavetable_buffer.(~buf, f);

~s = SoundFile(f)
~s.plot
~buf.plotWavetable




(
// default with vertical layout
w = Window.new.front;
w.view.decorator = FlowLayout(w.view.bounds);
g = EZListView.new(w,
        230@230,
        "An ListView:",
        [
                \item0 ->{ |a| ("this is item 0 of " ++ a).postln },
                \item1 ->{ |a| ("this is item 1 of " ++ a).postln },
                \item2 ->{ |a| ("this is item 2 of " ++ a).postln },
        ],
        globalAction: { |a| ("this is a global action of "++a.asString ).postln },
        initVal: 2,
        initAction: true,
        labelWidth: 120,
        labelHeight: 16,
        layout: \vert,
        gap: 2@2
        );

)




(
w = Window("plot panel", Rect(20, 30, 520, 250));
l = VLayoutView.new(w, Rect(0,0,400,200));
Slider.new(l, Rect(10, 10, 490, 20)).resize_(2).action_ { |v|
        a.value = (0..(v.value * 80).asInteger).scramble;
        w.refresh;
};
//z = CompositeView(w, Rect(10, 35, 490, 200)).background_(Color.rand(0.7)).resize_(5);
a = Plotter("plot", parent: l).value_([0, 1, 2, 3, 4].scramble * 100);
w.front;
)


(
Dialog.openPanel({ arg path;
        path.postln;
		},{
		        "cancelled".postln;

		});

)

(
~wd = ~class_load_wavetable_dialog.new
)
~wd.plotter.value_([1,5,7,8])
~wd.plotter.refresh
~wd.plotter.value.as(Array)
~wd.plotter.value.plot
~buf.play


{CrossoverDistortion.ar(LFPulse.ar([400, 404], 0, 0.2), MouseX.kr(0, 1), MouseY.kr(0, 1))}.play
{SmoothDecimator.ar(LFSaw.ar([400, 404]+SinOsc.ar(1,mul:50), 0, 0.2), MouseX.kr(0, 44100), MouseY.kr(1, 32))}.play


{Out.ar(0,Brusselator.ar(Impulse.kr(MouseY.kr(1,50,'exponential')),MouseX.kr(0.01,1,'exponential')))}.play


//fun time, stable oscillation with these parameters
{var mu = MouseY.kr(1.0,1.5); Out.ar(0,Pan2.ar(Brusselator.ar(0.0,MouseX.kr(0.01,0.1,'exponential'),mu,0.25)[0] ) )}.play


{ Out.ar(0,Pan2.ar(Brusselator.ar(Impulse.kr(10),MouseX.kr(0.01,0.1,'exponential'),1.5,0.25,MouseY.kr(-1.0,1.0),0.0)[0]) )}.play


{ Out.ar(0,Brusselator.ar(Impulse.kr(MouseY.kr(1,500,'exponential')),MouseX.kr(0.01,0.1,'exponential'),1.15,0.5,0.5,1.0) )}.play


(
var build_spread_array;
build_spread_array = { arg unisono;
	var z, ret;
	if(unisono.odd) {
		z = (unisono-1 / 2).asInteger;
		ret = z.collect { arg i; (i+1)/z };
		ret = 0-ret.reverse ++ 0 ++ ret;
	} {
		z = (unisono / 2).asInteger;
		ret = z.collect { arg i; (i+1)/z };
		ret = 0-ret.reverse ++ ret;
	};
};

10.do { arg x; [x,build_spread_array.(x)* -12].debug("x") }

)

(
w = Window.new;
v = ModSlider.new(w, Rect(10,10,50,150));
v.range1 = -0.5;
v.range2 = 0.5;
v.range3 = 0.8;
v.polarity2 = \bipolar;
//v.background = Color.white;
w.front;
)

(
// horizontal
w = Window.new;
v = ModSlider.new(w, Rect(10,10,150,50));
v.range1 = -0.5;
v.range2 = 0.5;
v.range3 = 0.8;
v.polarity2 = \bipolar;
//v.background = Color.white;
w.front;
)


(
~class_ktrenv_widget = (
	new: { arg self, parent, size;
		self = self.deepCopy;
		size = size ?? Rect(10,10,450,350);
		self.env_view = EnvelopeView.new(parent, size);
		//self.env_view.drawFunc { arg view;
		//	Pen.line(0,10);
		//	Pen.stroke;
		//
		//};
		self.env_view.value_([[0,1/4,2/4,3/4,1],[0,1/4,2/4,3/4,1]]);
		self.env_view.step_(1/128);
		self.env_view.selectionColor = Color.red;
		self.env_view.action = { arg view;
			var val;
			val = view.value;
			if(view.index == 0) {
				val[0][view.index] = 0;
			};
			if(view.index == (val[0].size-1)) {
				val[0][view.index] = 1;
			};
			val[0].sort;
			view.value = val;
		};
		self;
	
	},

	set_linear: { arg self;
		self.env_view.value_([[0,1/4,2/4,3/4,1],[0,1/4,2/4,3/4,1]]);
	},

	set_custom: { arg self, custom;
		self.env_view.value_(custom);
	},

	set_off: { arg self;
		self.env_view.value_([[0,1/4,2/4,3/4,1],[0,0,0,0,0]]);
	},

	transfert_function: { arg self, val, scale=1;
		var env = self.env_view.value;
		e = Env(env[1], env[0][1..].differentiate);
		e.at(val/scale)*scale;
	}

);
// horizontal
w = Window.new;
v = ~class_ktrenv_widget.new(w);
//v.background = Color.white;
w.front;
)
v.set_off
v.transfert_function(28,128).trunc
v.set_linear
v.x = 0.1
v.getEnv
v.refresh
v.dump
~v = v.value

e = Env(~v[1], ~v[0][1..].differentiate)
e.at(0.1)
~v[0][1..].differentiate
~v

(
// horizontal
w = Window.new;
v = EnvelopeView.new(w, Rect(10,10,450,350));
v.setEnv(e);
w.front;
)

(
e = Env([1, 2], [10]);
w = Window("Env Editor", Rect(200, 200, 300, 200));
v = SCEnvelopeEdit(w, w.view.bounds.moveBy(20, 20).resizeBy(-40, -40), e, 20).resize_(5);
w.front;
)


b = Buffer.alloc(s, 512, 1)
b.get(0, {|msg| msg.postln});
(
~funsig =  { arg x;
	var y;
	x = x % 1;
	y = sin(exp(x*2)*sin(x));
	y;
};
~load_curve_in_buffer.(b, ~funsig);
b.plot;
)



f = "/home/ggz/Musique/archwavetable/Architecture Waveforms 2010 Wav24/Architecture Waveforms 2010 Wav24/Misc - Artificial/Broken Bits.wav"
~sig = ~load_sample_as_signal.(f)

~sig[0.54 * ~sig.size]
0.54 * ~sig.size

~ss = Signal[0,1]
~ss[0.4]
