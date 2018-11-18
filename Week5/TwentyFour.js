const fs = require('fs');

class TFQuarantine {

    constructor(func) {
        this.funcs = [func];
    }

    bind(func) {
        this.funcs = this.funcs.concat(func);
        return this;
    }

    execute() {
        function guard_callable(v) {
            if (v instanceof Function) return v();
            else return v;
        }
        var value = function() {
            return;
        };
        
        this.funcs.forEach(function(func, index){
            value = func(guard_callable(value));
        });
        guard_callable(value);
    }
}




function get_input(arg) {
    function f() {
        return process.argv[2];
    }
    return f;
}


function extract_words(path_to_file) {
    function f() {
        var data = fs.readFileSync(path_to_file, 'utf8');
        var pattern = /[^a-zA-Z0-9+]+/gi;
        var word_list = data.replace(pattern, ' ').toLowerCase().split(' ');
        return word_list;
    }
    return f;
}

function remove_stop_words(word_list) {
    function f() {
        var data = fs.readFileSync("../stop_words.txt", 'utf8');
        var stop_words = data.split(',');
        var single_chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789'.split('');
        stop_words = stop_words.concat(single_chars);
        word_list = word_list.filter((word) => {
            for (var stop_word of stop_words) {
                if (stop_word == word) {
                    return false;
                }
            }
            return true;
        });

        return word_list;
    }
    return f;
}


function frequencies(word_list) {
    var word_freqs = {};
    for (var item of word_list) {
        if (word_freqs[item]) {
            word_freqs[item] = word_freqs[item] + 1;
        }
        else {
            word_freqs[item] = 1;
        }
    }
    return word_freqs;
}

function sort(dict) {
    var pairs = Object.keys(dict).map(function(key) {
        return [key, dict[key]];
    });

    // Sort the array based on the second element
    pairs.sort(function(first, second) {
        return second[1] - first[1];
    });
    return pairs;
}

function top25_freqs(word_freqs) {
    function f() {
        for (var values of word_freqs.slice(0,25)) {
            console.log(values[0] + "-" + values[1]);
        }
    }
    return f;
}

new TFQuarantine(get_input)
    .bind(extract_words)
    .bind(remove_stop_words)
    .bind(frequencies)
    .bind(sort)
    .bind(top25_freqs)
    .execute();