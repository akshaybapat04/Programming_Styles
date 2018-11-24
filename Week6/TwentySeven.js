const fs = require('fs');

function isalnum(c) {
    if (('A'<=c && c<='Z') || ('a'<=c && c<='z') || ('0'<=c && c<='9')){
        return true;
    }
    return false;
}

function sort(wordFreqs) {
    var items = Object.keys(wordFreqs).map(function(key) {
        return [key, wordFreqs[key]];
    });

    // Sort the array based on the second element
    items.sort(function(first, second) {
        return second[1] - first[1];
    });

    return items;
}

function* characters(pathToFile) {
  var data = fs.readFileSync(pathToFile, 'utf8');
  for (var i in data) {
//      console.log(data[i]);
      yield data[i];
    }
}

function* all_words(fileName) {
  var pattern = /[^a-zA-Z0-9+]+/gi;
  var lineGenerator = characters(fileName);
  var line = lineGenerator.next();
  while (!line.done){
      lineVal = line.value;
      data = lineVal.replace(pattern, ' ');
      data = data.toLowerCase();
      var wordList = data.split(' ');
      for (var i in wordList) {
          yield wordList[i];
      }
      line = lineGenerator.next();
  }
}

function* non_stop_words(fileName) {
    var data = fs.readFileSync("../stop_words.txt", "utf8");
    var stopWords = data.split(',');
    var wordGenerator = all_words(fileName);
    var word = wordGenerator.next();
    while (!word.done) {
        word_val = word.value.toLowerCase();
        if (!(word_val.length<2|| stopWords.includes(word_val))) {
            yield word_val;
            word = wordGenerator.next();
        } else {
            word = wordGenerator.next();
        }

    }
}

function* count_and_sort(fileName) {
    var freqs = {};
    var i = 1;
    var nonStopWordsGen = non_stop_words(fileName);
    var nonStopWord = nonStopWordsGen.next();
    while (!nonStopWord.done) {
        var word = nonStopWord.value;
        if (word in freqs) {
            freqs[word] += 1
        } else {
            freqs[word] = 1;
        }
        if (i%5000 == 0) {
            yield sort(freqs);
        }
        i++;
        nonStopWord = nonStopWordsGen.next();
    }
    yield sort(freqs);
}

var gen = count_and_sort('../pride-and-prejudice.txt');
var top = gen.next();
while (!top.done) {
    console.log('-------------');
    var top_words = top.value;
    for (var i in top_words.slice(0, 25)) {
        console.log(top_words[i][0] + '-' + top_words[i][1]);
    }
    top = gen.next();
}
