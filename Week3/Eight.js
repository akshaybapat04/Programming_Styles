const fs = require('fs');

function read_file(path_to_file, func) {
  fs.readFile(path_to_file, 'utf8', (err, data) => {
	  func(data, normalize);
  });
}

function filter_chars(str_data, func) {
  var pattern = /[^a-zA-Z0-9+]+/gi;
  func(str_data.replace(pattern, ' '), scan);
}

function normalize(str_data, func) {
  func(str_data.toLowerCase(), remove_stop_words);
}

function scan(str_data, func) {
  func(str_data.split(' '), frequencies);
}

function remove_stop_words(word_list, func) {
  fs.readFile('../stop_words.txt', 'utf8', (err, data) => {
      var filtered_words= [];
      var stop_words = data.split(',');
      var single_chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789'.split('');
      stop_words = stop_words.concat(single_chars);
      for (idx in word_list) {
        word = word_list[idx];
        if (!stop_words.includes(word)) {
          filtered_words.push(word);
        }
      }
      func(filtered_words, sort);
  });
 }

function frequencies(word_list, func) {
  var wf = {};
  for (idx in word_list) {
    word = word_list[idx];
    var count = wf[word] || 0;
    wf[word] = count+1;
  }
  func(wf, print_text);
}

function sort(wf, func) {
  var items = Object.keys(wf).map(function(key) {
	  return [key, wf[key]];
	});

  items.sort(function(key, val) {
	  return val[1] - key[1];
	});

  func(items.slice(0, 25), noOp);
}

function print_text(word_freqs, func) {
  for (key in word_freqs) {
    var values = word_freqs[key];
    console.log(values[0] + '-' + values[1]);
  }
}

function noOp(func) {
  return;
}

read_file(process.argv[2], filter_chars);