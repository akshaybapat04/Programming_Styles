var fs = require('fs');

function extract_words(me, path_to_file) {
	var data = fs.readFileSync(path_to_file, 'utf8');
	var pattern = /[^a-zA-Z0-9+]+/gi;
	data = data.replace(pattern, ' ');
	data = data.toLowerCase().split(' ');
	me.data = data;
}

function load_stop_words(me) {
    var stop_words = fs.readFileSync("../stop_words.txt", 'utf8').split(',');
    var single_chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789'.split('');
    stop_words = stop_words.concat(single_chars);
    me.stop_words = stop_words;
}


function increment_count(me, w) {
	if(me.freqs[w]) {
		me.freqs[w] = me.freqs[w] + 1;
	}
	else {
		me.freqs[w] = 1;
	}
}

var data_storage_obj = {
	data : [],
	init : function (path_to_file) { extract_words(data_storage_obj, path_to_file); },
	words : function () {	return data_storage_obj.data;}
};

var	stop_words_obj = {
		stop_words : [],
		init : function () { load_stop_words(stop_words_obj); },
		is_stop_word : function (word) { return  stop_words_obj.stop_words.includes(word); }
};

var word_freqs_obj = {
	freqs: {},
	increment_count : function(w) { increment_count(word_freqs_obj,w); },
	sorted : function () {
	var iteritems = Object.keys(word_freqs_obj.freqs).map(function(key) {
	  return [key, word_freqs_obj.freqs[key]];
	});

	// Sort the array based on the frequencies
	iteritems.sort(function(item1, item2) {
	  return item2[1] - item1[1];
	});
	return iteritems.slice(0, 25);
	}
};

data_storage_obj.init(process.argv[2]);
stop_words_obj.init();

for (var w in data_storage_obj.words()) {
    var word = data_storage_obj.words()[w];
	if(!stop_words_obj.is_stop_word(word)) {
		word_freqs_obj.increment_count(word);
	}
}

var word_freqs = word_freqs_obj.sorted();

for (var key in word_freqs) {
	console.log(word_freqs[key][0] + "-" + word_freqs[key][1]);
}
