/*
Afonso Goncalves | ist189399

Manage Element arrays
*/

#include "functions.h"

char less(Element a, Element b, char keyFlag){
	/*Compares two elements*/
	if(key(a, keyFlag) == key(b, keyFlag)){
		/*If equal first param, compare with second param*/
		return key(a, !keyFlag) < key(b, !keyFlag);
	} else {
		return key(a, keyFlag) < key(b, keyFlag);
	}
}

void myquicksort(Element a[], int l, int r, char keyFlag){
	/*Quicksort adapted to Element struct*/
	int i;
	if (r <= l)
		return;
	i = mypartition(a, l, r, keyFlag);
	myquicksort(a, l, i-1, keyFlag);
	myquicksort(a, i+1, r, keyFlag); 
}

int mypartition(Element a[], int l, int r, char keyFlag){
	/*Quicksort adapted to Element struct*/
	int i = l-1;
	int j = r;
	Element pivot = a[r];
	while (i <  j){
		while (less(a[++i], pivot, keyFlag));
		while (less(pivot, a[--j], keyFlag)){
			if (j == l){
				break;
			}
		}
		if (i < j){
			swap(a[i],a[j]);
		}
	}
	swap(a[i], a[r]);
	return i; 
}

void shiftRight(Element vec[], int l, int r){
	/*Shifts every Element in position l untill position r, inclusive*/
	int i;

	/*prevents misscalls. still shifts*/
	if (l == 0) l++;

	for(i = l; i<r; i++){
		vec[i-1] = vec[i];
	}
}