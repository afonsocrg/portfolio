from grammar import isWord
from itertools import permutations
import copy


'''---------------------------------
              GENERAL
---------------------------------'''
def gen_all_valid_words(letters):
    ''' TUPLE > LIST
    Generates every possible valid word with letters from <letters>
    '''
    valid_words = new_WordCollector()
    for numLetters in range(1, len(letters) + 1):
        #Every combination of words from <letters>
        every_permutation = list(permutations(letters, numLetters))
        for comb in every_permutation:
            word = ''
            for letter in comb: #creates word from array of letters
                word += letter
            if isWord(word): #if valid word
                possible_word = new_possible_word(word, letters) #create valid word
                add_word(valid_words, possible_word)      #add word to return array
    return valid_words

def guru_mp(valid_letters):
    '''TUPLE > NONE
    Manages players and turns (main function of the game)
    '''

    if not is_valid_arg(valid_letters, tuple):
        raise ValueError('guru_mj:invalid argument.')

    print('Find every valid word using the following letters:\n'+
        str(valid_letters))
    print('Insert Player names (-1 to break)...')
    name = ''
    player_number = 1
    player_list = []
    
    while name != '-1':
        name = input('PLAYER ' + str(player_number) + ' -> ')
        player_number += 1
        if name != '-1':
            player_list += [new_Player(name)]

    number_players = len(player_list)
    turn_number = 1

    to_find = gen_all_valid_words(valid_letters) #every valid word possible
    howMany_to_find = number_of_words(to_find) #number of valid words
    found_words = new_WordCollector() #found valid words (constructor function)

    while howMany_to_find > 0:
        player_index = (turn_number-1)%number_players #get's next player
        player = player_list[player_index]

        print(str(turn_number)+' - '+ str(howMany_to_find)+' words left')

        player_try = input('player '+player_name(player)+' -> ')
        player_word = new_possible_word(player_try, valid_letters)
        
        if isWord(player_word):
            print(player_word + ' - Valid word :D')
            if player_word not in found_words:
                add_word(found_words, player_word)
                howMany_to_find -= 1
                add_valid_word(player, player_word)
        else:
            print(player_word + ' - palavra INVALIDA')
            add_invalid_word(player, player_word)
        
        turn_number += 1

    points = {} #players sorted by ponctuation
    for player in player_list:
        if str(player_points(player)) not in points:
            points[str(player_points(player))] = [player]
        else:
            points[str(player_points(player))] += [player]

    #find max ponctuation
    if len(player_list) >= 1:    
        pont_max = player_points(player_list[0])
    for p in points:
        p_val = int(p)
        if p_val > pont_max:
            pont_max = p_val

    pont_max = str(pont_max)
    if len(points[pont_max]) > 1:
        print('THE END! Tied game...')
    else:
        winner = points[pont_max][0]
        print('THE END! The winner is: '+
            player_name(winner)+        
            ' (' + str(player_points(winner)) + ' points).')

    for player in player_list:
        print(player_stringify(player))
    return

#FUNCOES AUXILIARES CHAMADAS POR FUNCOES DIFERENTES
def is_valid_arg(arg, wanted_type):
    ''' STR/TUPLE, TYPE > BOOL
    True if valid argument, according to wanted type
    '''
    def valid_elements(arg_teste):
        '''
        True if made of capital letters between A and Z
        '''
        def between_A_Z(char):
            ''' STRING > BOOL
            Capital letter
            '''
            #ord('A') = 65; ord('Z') = 90
            return 65<= ord(str(char)) <= 90

        for element in arg_teste:
            length = len(element)
            if (not isinstance(element, str) or length>1 or                                          
            (length == 1 and not between_A_Z(element))):
                return False #false if not capital character between A and Z
        return True

    if wanted_type not in (str, tuple):
        raise ValueError('is_valid_arg:invalid arguments')
    return isinstance(arg, wanted_type) and valid_elements(arg)
    #tipo especificado e ele. todos validos

def is_valid_word(arg1, arg2):
    ''' STR, TUPLE > BOOL
    True if arg1 is made of letters from arg2 with no repetition
    '''    
    if not (isinstance(arg1, str) and isinstance(arg2, tuple)):
        return False

    available_letters = list(arg2)

    for letter in arg1:
        len_before_testing = len(available_letters)
        for pos in range(len(available_letters)):
            if available_letters[pos] == letter:
                del(available_letters[pos])
                break                                                                               
        
        if len(available_letters) == len_before_testing:
            return False

    return True


'''---------------------------------
        possible_word
---------------------------------'''
def new_possible_word(string, valid_letters):
    ''' STR, TUPLE > STR
    return possible_word if string can be built from valid_letters
    '''
    if not (is_valid_arg(string, str)and is_valid_arg(valid_letters, tuple)):
        raise ValueError('new_possible_word:invalid arguments.')
    
    elif not is_valid_word(string, valid_letters):
        raise ValueError('new_possible_word:invalid word.')
    return string

def is_possible_word(arg1):
    '''all > BOOL
    True if possible word
    '''
    return is_valid_arg(arg1, str)

def word_len(p_pot):
    ''' PALAVRA POTENCIAL > INT
    Devolve o numLetters de possible_word
    '''
    if not is_possible_word(p_pot):
        raise ValueError('word_len:invalid argument.')
    return len(p_pot)

def equal_possible_words(poss_word_1, poss_word_2):
    '''possible_word, possible_word > BOOL
    True if equal possible words
    '''
    if not(is_possible_word(poss_word_1) and is_possible_word(poss_word_2)):
        raise ValueError('equal_possible_words:invalid arguments.')
    return poss_word_1 == poss_word_2

def possible_word_less(poss_word_1, poss_word_2):
    '''possible_word, possible_word > BOOL
    True if poss_word_1 is less than poss_word_2 (alphabetically)
    '''
    if not(is_possible_word(poss_word_1) and is_possible_word(poss_word_2)):
        raise ValueError('possible_word_less:invalid arguments.')
    
    #keeping abstraction
    first_possWord = possible_word_stringify(poss_word_1)                                               
    secnd_possWord = possible_word_stringify(poss_word_2)

    sorted_words = sorted((first_possWord, secnd_possWord))
    return first_possWord == ordenadas[0]

def possible_word_stringify(possible_word):
    '''possible_word > STR
    stringifies possible_word
    '''
    if not is_possible_word(possible_word):
        raise ValueError('possible_word_stringify:invalid argument.')
    return possible_word


'''---------------------------------
          WordCollector
---------------------------------'''
def new_WordCollector():
    ''' NONE > LIST
    create new word collector
    '''
    return []

def number_of_words(wordCollector):
    ''' wordCollector > INT
    return number of words in wordCollector
    '''
    return len(wordCollector)

def words_by_size(wordCollector, numLetters):
    '''wordCollector, INT > LIST
    returns every letter in wordCollector that is made of n letters
    '''
    out = []
    for word in wordCollector:
        if word_len(word) == numLetters:
            out += [word]
    return out

def add_word(collector, word):
    '''wordCollector, possible_word > NONE
    Add new word to wordCollector
    '''
    if not (is_wordCollector(collector) and 
        is_possible_word(word)):
        raise ValueError('add_word:invalid arguments.')
    
    if word not in collector:
        collector += [word]
    return

def is_wordCollector(arg1):
    '''wordCollector > BOOL
    True if arg is a wordCollector
    '''
    if not isinstance(arg1, list):
        return False
    for el in arg1:                                                                      
        if not is_possible_word(el):
            return False
    return True

def equal_word_collectors(wc1, wc2):
    '''wordCollector, wordCollector > BOOL
    True if equal wordCollectors
    '''
    if not (is_wordCollector(wc1) and is_wordCollector(wc2)):
        raise ValueError('equal_word_collectors:invalid arguments.')
    
    for word in wc1:                                                                          
        if word not in wc2:
            return False
    return number_of_words(wc1) == number_of_words(wc2)

def word_in_index(wCollector, index):
    '''wordCollector, INT > possible_word
    Return word in index <index>
    '''
    if (number_of_words(wCollector) == 0 or
        index>number_of_words(wCollector)-1):
        return ''
    else:
        return wCollector[index]

def alphabetical_qSort(wordCollector):
    '''wordCollector > wordCollector
    Return ordered wordCollector
    '''
    def word_filter(collector, word, less_flag):
        '''wordCollector, flag > wordCollector
        Splits words, according to pivot
        '''
        res = new_WordCollector()
        for index in range(1, number_of_words(collector)):
            curr_word = word_in_index(collector, index)
            if possible_word_less(curr_word, word) and less_flag:
                add_word(res, curr_word)
            elif not(possible_word_less(curr_word, word) or
                less_flag):
                add_word(res, curr_word)
        return res
    
    if number_of_words(wordCollector) <= 1:
        return wordCollector
    else:
        pivot = word_in_index(wordCollector, 0)
        smaller_words =  word_filter(wordCollector, pivot, True)
        lista_maiores = word_filter(wordCollector, pivot, False)

        smaller_words_final = alphabetical_qSort(smaller_words)
        lista_maiores_final = alphabetical_qSort(lista_maiores)
    
    output = new_WordCollector()
    for word in smaller_words_final:
        add_word(output, word)
    add_word(output, pivot)
    for word in lista_maiores_final:
        add_word(output, word)
    return output


def wordCollector_stringify(arg1):
    '''wordCollector > STR
    stringifies word collector
    '''
    if not is_wordCollector(arg1):
        raise ValueError('wordCollector_stringify:invalid argument.')
    
    if number_of_words(arg1) == 0:
        return '[]'

    words_by_size = {}
    max_size = 0
    for word in arg1:
        size = word_len(word)
        if size not in words_by_size:
            words_by_size[size]=[possible_word_stringify(word)]
        else:
            words_by_size[size]+=[possible_word_stringify(word)]
        
        if size > max_size:
            max_size = size
    
    for size in words_by_size:
        words_by_size[size]=alphabetical_qSort(words_by_size[size])
    
    output = ['['] #list, so it's possible to remove last ";"
    for i in range(max_size+1):
        if i in words_by_size:
            output+=[str(i)]+['->']+[str(words_by_size[i])]+[';']

    output[len(output)-1] = ']'
    
    output_final = ''
    for item in output:
        output_final += item

    return output_final.replace("'", '')


'''---------------------------------
              player
--------------------------------'''
def new_Player(name):
    '''STR > player
    creates new player
    '''
    if not isinstance(name, str):
        raise ValueError('new_Player:invalid argument.')
    
    return {
    'NAME': name,
    'POINTS': 0,
    'VALID': new_WordCollector(),
    'INVALID': new_WordCollector()
    }

def player_name(player):
    '''player > STR
    Return player name
    '''
    if not is_player(player):
        raise ValueError('player_name:invalid argument.')
    return player['NAME']

def player_points(player):
    '''player > INT
    Return player points
    '''
    if not is_player(player):
        raise ValueError('player_points:invalid argument.')
    return player['POINTS']

def player_valid_words(player):
    '''player > wordCollector
    Return valid plays
    '''
    if not is_player(player):
        raise ValueError('player_valid_words:invalid argument.')
    return player['VALID']

def player_invalid_words(player):
    '''player > wordCollector
    return invalid plays
    '''
    if not is_player(player):
        raise ValueError('player_invalid_words:invalid argument.')
    return player['INVALID']

def is_player(arg1):
    '''arg1 > BOOL
    True if arg is a player
    '''
    if not isinstance(arg1, dict):
        return False

    for key in arg1:
        if (key not in ('NAME', 'POINTS', 'VALID', 'INVALID')
            or (key == 'NAME' and not isinstance(arg1[key], str))
            or (key == 'POINTS' and not isinstance(arg1[key], int))
            or (key in ('VALID', 'INVALID') and
                not is_wordCollector(arg1[key]))):
            return False
    return len(arg1) != 0

def add_valid_word(player, played_word):
    '''player, possible_word >
    adds valid word to player. updates points
    '''
    if not(is_player(player) and is_possible_word(played_word)):
        raise ValueError('add_valid_word:invalid arguments.')
    
    bak = copy.deepcopy(player_valid_words(player))
    add_word(bak, played_word)
    if number_of_words(bak)!=number_of_words(player_valid_words(player)): #if new word
        add_word(player_valid_words(player), played_word)
        player['POINTS'] += word_len(played_word)
    return

def add_invalid_word(player, possible_word):
    '''player, possible_word >
    adds invalid word. updates points
    '''
    if not(is_player(player) and is_possible_word(possible_word)):
        raise ValueError('add_invalid_word:invalid arguments.')
    
    bak = copy.deepcopy(player_invalid_words(player))
    add_word(bak, possible_word)
    if number_of_words(bak)!=number_of_words(player_invalid_words(player)): #if new word
        add_word(player_invalid_words(player), possible_word)
        player['POINTS'] -= word_len(possible_word)
    return

def player_stringify(player):
    '''player > STR
    return player info
    '''
    if not is_player(player):
        raise ValueError('player_stringify:invalid argument.')    
    
    output = ''
    for key in ('player', 'POINTS', 'VALID', 'INVALID'):
        output += key
        if key == 'player':
            output += ' ' + player[key]
        elif key in ('VALID', 'INVALID'):
            output+='='+wordCollector_stringify(player[key])                      
        else:
            output += '=' + str(player[key])                                            
        
        if key != 'INVALID':
            output += ' '


    return output.replace("'", '')

#insert in tuple letters to play with!
guru_mp(("A", "T", "U", "M"))