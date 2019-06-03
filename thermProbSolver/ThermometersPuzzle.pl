%-----------------------------------------------------------------------------%
%                        getLine(Coordinate, Line)                            %
%-----------------------------------------------------------------------------%
%            Line corresponds to the first part of Coordinate                 %
%-----------------------------------------------------------------------------%
getLine((L, _), L).


%-----------------------------------------------------------------------------%
%                          getCol(Coordinate, Col)                            %
%-----------------------------------------------------------------------------%
%             Col corresponds to the second part of Coordinate                %
%-----------------------------------------------------------------------------%
getCol((_, C), C).


%-----------------------------------------------------------------------------%
%                           notMember(Element, List)                          %
%-----------------------------------------------------------------------------%
%                         Element doesnt belong to List                       %
%-----------------------------------------------------------------------------%
notMember(Element, List):- \+member(Element, List).


%-----------------------------------------------------------------------------%
%                  propagateList(Puz, List, Count, Positions)                 %
%-----------------------------------------------------------------------------%
%                         Propagates list of positions                        %
%-----------------------------------------------------------------------------%
propagateList(_, [], Count, Count).
propagateList(Puz, [H|T], Count, Positions):-
    propagate(Puz, H, NewPos),
    union(Count, NewPos, NewCount),
    propagateList(Puz, T, NewCount, Positions), !.


%-----------------------------------------------------------------------------%
%                        propagate(Puz, Position, Positions)                  %
%-----------------------------------------------------------------------------%
%    Positions is a list of all the parts of one of the thermometer between   %
%   its head and the given Position                                           %
%-----------------------------------------------------------------------------%
propagate([Thermometers|_], Position, Positions):-
    findTherm(Thermometers, Position, Therm),
    fillTherm(Therm, Position, [], Partial),
    sort(Partial, Positions), !.


%-----------------------------------------------------------------------------%
%                  findTherm(Thermometers, Pos, Thermometer)                  %
%-----------------------------------------------------------------------------%
%    Pos belongs to Thermometer                                               %
%-----------------------------------------------------------------------------%
findTherm([], _, []).
findTherm([Therm|_], Pos, Therm):-
    member(Pos, Therm).
findTherm([H|T], Pos, Therm):-
    notMember(Pos, H),
    findTherm(T, Pos, Therm).


%-----------------------------------------------------------------------------%
%                 fillTherm(Thermometer, Pos, Cont, Positions)                %
%-----------------------------------------------------------------------------%
%    Positions is a list of all the parts of the thermometer between its      %
%   head and the given Position                                               %
%-----------------------------------------------------------------------------%
fillTherm([], _, _, _) :- fail.
fillTherm([H|_], H, Count, Positions):-
    append(Count, [H], Positions), !.
fillTherm([H|T], Pos, Count, Positions):-
    append(Count, [H], NewCount),
    fillTherm(T, Pos, NewCount, Positions).


%-----------------------------------------------------------------------------%
%            keeps_prev_lines(New_Pos, NumLine, Already_Filled)               %
%-----------------------------------------------------------------------------%
%    No position from New_Pos fills any previous line of the puzzle           %
%-----------------------------------------------------------------------------%
keeps_prev_lines([], _, _).
keeps_prev_lines([H|T], LineNumber, Already_Filled):-
    getLine(H, CurrLine),
    (CurrLine >= LineNumber ->
        keeps_prev_lines(T, LineNumber, Already_Filled)
    ;
        member(H, Already_Filled),
        keeps_prev_lines(T, LineNumber, Already_Filled)
    ).


%-----------------------------------------------------------------------------%
%               semi_Verify(Puz, Already_Filled, Dim, Poss)                   %
%-----------------------------------------------------------------------------%
%    Already_Filled positions dont surpass the collumn limits of the puzzle   %
%-----------------------------------------------------------------------------%
semi_Verify(Puz, Already_Filled, Curr_Col, Poss):-
    union(Already_Filled, Poss, Every_Poss),
    semi_Verify_aux(Puz, Already_Filled, Curr_Col, Poss, Every_Poss), !.

    %-------------------------------------------------------------------------%
    % semi_Verify_aux(Puz, Already_Filled, Curr_Col, Poss, Every_Poss)        %
    %                                                                         %
    %    verifies collumn limits one by one                                   %
    %-------------------------------------------------------------------------%
semi_Verify_aux(_, _, 0, _, _).
semi_Verify_aux(Puz, Already_Filled, Curr_Col, Poss, Every_Poss):-
    countLine(Every_Poss, Curr_Col, getCol, 0, TotalCol),
    nth1(3, Puz, Col_Limits),
    nth1(Curr_Col, Col_Limits, MaxCol),
    TotalCol =< MaxCol, !,
    New_Col is Curr_Col-1,
    semi_Verify_aux(Puz, Already_Filled, New_Col, Poss, Every_Poss), !.


%-----------------------------------------------------------------------------%
%                countLine(List, NumRowCol, Key, Count, Total)                %
%-----------------------------------------------------------------------------%
%    List has Total elements in each Row/Col (depends on Key)                 %
%-----------------------------------------------------------------------------%
countLine([], _, _, Count, Count).
countLine([H|T], NumRowCol, Key, Count, Total):-
    call(Key, H, ValRowCol),
    (ValRowCol =:= NumRowCol ->
        NewCount is Count + 1
    ;   
        NewCount is Count
    ),
    countLine(T, NumRowCol, Key, NewCount, Total).


%-----------------------------------------------------------------------------%
%        poss_Line(Puz, Position_Line, Total, Already_Filled, PossLine)       %
%-----------------------------------------------------------------------------%
%    PossLine is a list of every possible line solution                       %
%-----------------------------------------------------------------------------%
poss_Line(Puz, Position_Line, Total, Already_Filled, PossFinal):-
    %Get number of line and dimension of the puzzle
    nth0(0, Position_Line, El),
    getLine(El, NumLine),
    length(Position_Line, Dim),
    findall(
        Poss,
        possibleLine(Puz, Position_Line, Total, Already_Filled, NumLine, Dim, Poss),
        Every_Posss),
    sort(Every_Posss, PossFinal).


%-----------------------------------------------------------------------------%
% possibleLine(Puz, Position_Line, Total, Already_Filled, NumLine, Dim, Poss) %
%-----------------------------------------------------------------------------%
%    PossLine is a list of one possible line solution                         %
%-----------------------------------------------------------------------------%
possibleLine(Puz, Position_Line, Total, Already_Filled, NumLine, Dim, Poss):-
    intersection(Position_Line, Already_Filled, Already_FilledLine),
    subtract(Position_Line, Already_FilledLine, Choices),
    length(Already_FilledLine, NumElementsChosen),
    LeftToChoose is Total - NumElementsChosen,
    combN(LeftToChoose, Choices, Choice),
    union(Choice, Already_FilledLine, ChoiceLine),
    propagateList(Puz, ChoiceLine, [], Propagated),
    intersection(Position_Line, Propagated, ElemLine),
    length(ElemLine, TotalLine),
    TotalLine == Total,
    keeps_prev_lines(Propagated, NumLine, Already_Filled),
    semi_Verify(Puz, Already_Filled, Dim, Propagated),

    sort(Propagated, Poss).


%-----------------------------------------------------------------------------%
%                              solve(Puz, Sol)                                %
%-----------------------------------------------------------------------------%
%    Sol is a solution for Puz
%-----------------------------------------------------------------------------%
solve(Puz, Sol):-
    nth1(2, Puz, LineLimits),
    length(LineLimits, Dim),
    solve_aux(Puz, 0, Dim, LineLimits, [], Sol).

    %-------------------------------------------------------------------------%
    %       solve_aux(Puz, PrevLine, Dim, LineLimits, Count, Sol)             %
    %                                                                         %
    %    Get solution Line by Line                                            %
    %-------------------------------------------------------------------------%
solve_aux(_, Dim, Dim, _, Already_Filled, Sol):-
    sort(Already_Filled, Sol).
solve_aux(Puz, PrevLine, Dim, LineLimits, Already_Filled, Sol):-
    CurrLine is PrevLine + 1,
    getLine_Pos(CurrLine, 0, Dim, [], Position_Line),
    nth1(CurrLine, LineLimits, LimitLine),
    poss_Line(Puz, Position_Line, LimitLine, Already_Filled, Poss),
    member(Possibility, Poss),
    union(Already_Filled, Possibility, NewAlready_Filled),
    solve_aux(Puz, CurrLine, Dim, LineLimits, NewAlready_Filled, Sol).


%-----------------------------------------------------------------------------%
%           getLine_Pos(NumLine, PrevCol, Dim, Count, Final)                  %
%-----------------------------------------------------------------------------%
%    Final is a set of Positions belonging to NumLine line                    %
%-----------------------------------------------------------------------------%
getLine_Pos(_, Dim, Dim, Count, Count):- !.
getLine_Pos(NumLine, PrevCol, Dim, Count, Position_Line):-
    Curr_Col is PrevCol + 1,
    append(Count, [(NumLine, Curr_Col)], NewCount),
    getLine_Pos(NumLine, Curr_Col, Dim, NewCount, Position_Line).

%-----------------------------------------------------------------------------%
%                    combN(NumElements, List, Result)                         %
%-----------------------------------------------------------------------------%
%    Result is a set of NumElements elements from List                        %
%-----------------------------------------------------------------------------%
combN(0,_,[]).
combN(N,[H|T],[H|Comb]):-
    N>0,
    N1 is N-1,
    combN(N1,T,Comb).
combN(N,[_|T],Comb):-
    N>0,
    combN(N,T,Comb).
