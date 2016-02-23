#!/bin/bash
CURENT_PATH=$(pwd)
cd translation_helper/
rm -rf ./PillarsOfEternity_Data
rm ./with_expansion.zip ./with_expansion.7z ./without_expansion.zip ./without_expansion.7z
cd ..
# geht nur von eins weiter oben korrekt … versteh einer git
git checkout-index -a --prefix=translation_helper/PillarsOfEternity_Data/
cd translation_helper/
echo "Packing zip:"
7z a -tzip -mm=Deflate64 -mfb=257 -mpass=15 -mx=9 -xr@exclude.lst with_expansion.zip PillarsOfEternity_Data/ | awk '!/ing  /'
7z a -tzip -mm=Deflate64 -mfb=257 -mpass=15 -mx=9 -xr@exclude.lst -xr!data_expansion1\* -xr!data_expansion2\* without_expansion.zip PillarsOfEternity_Data/ | awk '!/ing  /'
echo "Packing 7z:"
7z a -t7z -m0=ppmd:mem=28:o=12 -mx=9 -bd -xr@exclude.lst with_expansion.7z PillarsOfEternity_Data/ | awk '!/ing  /'
7z a -t7z -m0=ppmd:mem=28:o=12 -mx=9 -bd -xr@exclude.lst -xr!data_expansion1\* -xr!data_expansion2\* without_expansion.7z PillarsOfEternity_Data/ | awk '!/ing  /'

rm -rf ./PillarsOfEternity_Data
cd $CURRENT_PATH
