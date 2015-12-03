#!/bin/sh
CURENT_PATH=$(pwd)
cd translation_helper/
rm -rf ./PillarsOfEternity_Data
rm ./with_expansion.zip ./with_expansion.7z ./without_expansion.zip ./without_expansion.7z
cd ..
# geht nur von eins weiter oben korrekt … versteh einer git
git checkout-index -a --prefix=translation_helper/PillarsOfEternity_Data/
cd translation_helper/

# der zip Befehl geht so nicht in bin/bash
7z a -tzip -mm=ppmd -mmem=256m -mo=16 -mx=9 -xr@exclude.lst with_expansion.zip PillarsOfEternity_Data/
7z a -tzip -mm=ppmd -mmem=256m -mo=16 -mx=9 -xr@exclude.lst -xr!data_expansion1\* without_expansion.zip PillarsOfEternity_Data/

7z a -t7z -m0=ppmd:mem=28:o=12 -mx=9 -bd -xr@exclude.lst with_expansion.7z PillarsOfEternity_Data/
7z a -t7z -m0=ppmd:mem=28:o=12 -mx=9 -bd -xr@exclude.lst -xr!data_expansion1\* without_expansion.7z PillarsOfEternity_Data/

rm -rf ./PillarsOfEternity_Data
cd $CURRENT_PATH
