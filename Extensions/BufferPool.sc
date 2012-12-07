BufferPool {

	classvar <counts,<annotations,<paths;

	*alloc { |client,name,numFrames,numChannels=1,server=nil|
		var prev,buf;
		server = server ?? Server.default;
		buf = Buffer.alloc(server, numFrames, numChannels);
		this.retain(buf,client,name);
		^buf
	}

	*read { |client,name,path, action=nil, server=nil|
		var prev,buf;
		server = server ?? Server.default;
		buf = Buffer.read(server, path, 0, -1, action);
		this.retain(buf,client,name);
		^buf
	}

	*read_mono { |client,name,path, action=nil, server=nil|
		var prev,buf;
		server = server ?? Server.default;
		buf = Buffer.readChannel(server, path, 0, -1, [0], action); //FIXME: can't choose the channel
		this.retain(buf,client,name);
		^buf
	}

	*get_sample { |client,path, action=nil, server=nil|
		var buf = paths.at(path); 
		paths.debug("paths");
		if(buf.notNil, {
			this.retain(buf,client,\void);
		}, {
			buf = this.read(client,\void,path,action,server);
			paths[path] = buf;
		});
		^buf
	}

	//TODO: write get_stereo_sample using readChannel [0, 0] if sample is mono
	*get_mono_sample { |client,path, action=nil, server=nil|
		var buf = paths.at("__mono__"++path); 
		paths.debug("paths");
		if(buf.notNil, {
			this.retain(buf,client,\void);
		}, {
			buf = this.read_mono(client,\void,path,action,server);
			paths["__mono__"++path] = buf;
		});
		^buf
	}

	*retain { |buf,client,name|
		if(annotations.at(buf,client).notNil,{
			(client.asString++" already retained buffer "++buf.path).warn;
		}, {
			counts.add(buf);
			annotations.put(buf,client,name);
			this.watchServer(buf.server);
		});
	}
	*release { |buf,client|
		var dict,key;
		if(annotations.at(buf,client).isNil,{
			(client++" already released buffer "++buf.path).warn;
		}, {
			counts.remove(buf);
			annotations.removeAt(buf,client);
			if(counts.itemCount(buf) == 0,{
				buf.free;
				//FIXME: mono samples never freed
				paths[buf.path] = nil;
			})
		});
	}

	*release_client { arg client;
		annotations.leafDo({ arg x, y;
			[x,y].debug("buf, client");
			if( x[1] == client ) { 
				x[0].debug("to free");
				this.release(x[0], x[1]);
			}
		})
	}

	*initClass {
		this.reset;
	}
	*reset {
		if(counts.notNil,{
			counts.contents.keysValuesDo({ |buf,count| buf.free });
		});
		counts = Bag.new;
		annotations = MultiLevelIdentityDictionary.new;
		paths = Dictionary.new;
	}
	*watchServer { |server|
		if(NotificationCenter.registrationExists(server,\newAllocators,this).not,{
			NotificationCenter.register(server,\newAllocators,this,{
				this.reset;
			});
		});
	}
	*itemCount { |buf| ^counts.itemCount(buf) }
	*buffers { ^counts.contents.keys.as(Array) }
}
