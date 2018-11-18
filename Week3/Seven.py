import re, sys, operator

# Mileage may vary. If this crashes, make it lower
RECURSION_LIMIT = 100
# We add a few more, because, contrary to the name,
# this doesn't just rule recursion: it rules the
# depth of the call stack
sys.setrecursionlimit(4*RECURSION_LIMIT+10)

def print_loop(print_func):
    def wf_print(wordfreq):
        if wordfreq == []:
            return
        else:
            (w, c) = wordfreq[0]
            print(w, '-', c)
            print_func(wordfreq[1:])
    return wf_print

def real_print_func(print_loop):
    return print_loop(print_diff_func)

def print_diff_func(wordfreq):
    return print_loop(print_diff_func)(wordfreq)

def count_loop(count_lambda):
    def count(word_list, stopwords, wordfreqs):
        if word_list == []:
            return
        else:
            word = word_list[0]
            if word not in stopwords:
                if word in wordfreqs:
                    wordfreqs[word] += 1
                else:
                    wordfreqs[word] = 1
            count_lambda(word_list[1:], stopwords, wordfreqs)
    return count

def real_count_func(count_loop):
    def dummy_count(word_list, stopwords, wordfreqs):
        return count_loop(dummy_count)(word_list, stopwords, wordfreqs)
    return count_loop(dummy_count)

count = real_count_func(count_loop)
wf_print = real_print_func(print_loop)


stop_words = set(open('../stop_words.txt').read().split(','))
words = re.findall('[a-z]{2,}', open(sys.argv[1]).read().lower())
word_freqs = {}
# Theoretically, we would just call count(words, word_freqs)
# Try doing that and see what happens.
for i in range(0, len(words), RECURSION_LIMIT):
    count(words[i:i+RECURSION_LIMIT], stop_words, word_freqs)

wf_print(sorted(word_freqs.items(), key=operator.itemgetter(1), reverse=True)[:25])
