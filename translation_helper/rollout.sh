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
zip with_expansion.zip -9 -r -q PillarsOfEternity_Data/ -x @exclude.lst
zip without_expansion.zip -9 -r -q PillarsOfEternity_Data/ -x @exclude.lst \*/data_expansion1/\*

7z a -t7z -m0=lzma -mx=9 -bd -xr@exclude.lst with_expansion.7z PillarsOfEternity_Data/
7z a -t7z -m0=lzma -mx=9 -bd -xr@exclude.lst -xr!data_expansion1\* without_expansion.7z PillarsOfEternity_Data/

rm -rf ./PillarsOfEternity_Data
cd $CURRENT_PATH
