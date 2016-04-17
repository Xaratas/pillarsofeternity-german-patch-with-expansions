#!/bin/bash
# Zählt line, word und char hinzufügungen und entfernungen, lässt die Anführungszeichen und reines XML gedöns weg. Beachtet nur modified files.
# Die Differenz von add und remove sollte von Version zu Version eine brauchbare Statistik abgeben. Tut sie aber nicht, weil die Patches massiv an den
# Dateien umgestellt haben. Besonders zwischen 0.7.5 und 0.8.5 kommen durch Stronghold, Patch 3.00 und Patch 3.01 viel zu hohe Werte raus.

echo "added"
for i in "data" "data_expansion1" "data_expansion2"; do
echo "$i"
# initialen import auslassen
git diff --word-diff=porcelain --diff-filter=M 3837ec75..v0.5.0 -- $i | grep -e '^+[^+]' | grep -v -e '[„“‚‘…]\|.*</\?\(Entry\|EntryCount\|ID\)>' | wc | xargs
git diff --word-diff=porcelain --diff-filter=M v0.5.0..v0.6.0 -- $i | grep -e '^+[^+]' | grep -v -e '[„“‚‘…]\|.*</\?\(Entry\|EntryCount\|ID\)>' | wc | xargs
git diff --word-diff=porcelain --diff-filter=M v0.6.0..v0.7.0 -- $i | grep -e '^+[^+]' | grep -v -e '[„“‚‘…]\|.*</\?\(Entry\|EntryCount\|ID\)>' | wc | xargs
git diff --word-diff=porcelain --diff-filter=M v0.7.0..v0.7.5 -- $i | grep -e '^+[^+]' | grep -v -e '[„“‚‘…]\|.*</\?\(Entry\|EntryCount\|ID\)>' | wc | xargs
git diff --word-diff=porcelain --diff-filter=M v0.7.5..v0.8.0 -- $i | grep -e '^+[^+]' | grep -v -e '[„“‚‘…]\|.*</\?\(Entry\|EntryCount\|ID\)>' | wc | xargs
git diff --word-diff=porcelain --diff-filter=M v0.8.0..v0.8.5 -- $i | grep -e '^+[^+]' | grep -v -e '[„“‚‘…]\|.*</\?\(Entry\|EntryCount\|ID\)>' | wc | xargs
git diff --word-diff=porcelain --diff-filter=M v0.8.5..v0.9.0 -- $i | grep -e '^+[^+]' | grep -v -e '[„“‚‘…]\|.*</\?\(Entry\|EntryCount\|ID\)>' | wc | xargs
git diff --word-diff=porcelain --diff-filter=M v0.9.0..HEAD -- $i | grep -e '^+[^+]' | grep -v -e '[„“‚‘…]\|.*</\?\(Entry\|EntryCount\|ID\)>' | wc | xargs
#git diff --word-diff=porcelain --diff-filter=M v0.9.0..v0.9.5 -- $i | grep -e '^+[^+]' | grep -v -e '[„“‚‘…]\|.*</\?\(Entry\|EntryCount\|ID\)>' | wc | xargs
done

echo "removed"
for i in "data" "data_expansion1" "data_expansion2"; do
echo "$i"
git diff --word-diff=porcelain --diff-filter=M 3837ec75..v0.5.0 -- $i | grep -e '^-[^-]' | grep -v -e '["]\|\.\{3\}\|.*</\?\(Entry\|EntryCount\|ID\)>' | wc | xargs
git diff --word-diff=porcelain --diff-filter=M v0.5.0..v0.6.0 -- $i | grep -e '^-[^-]' | grep -v -e '["]\|\.\{3\}\|.*</\?\(Entry\|EntryCount\|ID\)>' | wc | xargs
git diff --word-diff=porcelain --diff-filter=M v0.6.0..v0.7.0 -- $i | grep -e '^-[^-]' | grep -v -e '["]\|\.\{3\}\|.*</\?\(Entry\|EntryCount\|ID\)>' | wc | xargs
git diff --word-diff=porcelain --diff-filter=M v0.7.0..v0.7.5 -- $i | grep -e '^-[^-]' | grep -v -e '["]\|\.\{3\}\|.*</\?\(Entry\|EntryCount\|ID\)>' | wc | xargs
git diff --word-diff=porcelain --diff-filter=M v0.7.5..v0.8.0 -- $i | grep -e '^-[^-]' | grep -v -e '["]\|\.\{3\}\|.*</\?\(Entry\|EntryCount\|ID\)>' | wc | xargs
git diff --word-diff=porcelain --diff-filter=M v0.8.0..v0.8.5 -- $i | grep -e '^-[^-]' | grep -v -e '["]\|\.\{3\}\|.*</\?\(Entry\|EntryCount\|ID\)>' | wc | xargs
git diff --word-diff=porcelain --diff-filter=M v0.8.5..v0.9.0 -- $i | grep -e '^-[^-]' | grep -v -e '["]\|\.\{3\}\|.*</\?\(Entry\|EntryCount\|ID\)>' | wc | xargs
git diff --word-diff=porcelain --diff-filter=M v0.9.0..HEAD -- $i | grep -e '^-[^-]' | grep -v -e '["]\|\.\{3\}\|.*</\?\(Entry\|EntryCount\|ID\)>' | wc | xargs
#git diff --word-diff=porcelain --diff-filter=M v0.9.0..v0.9.5 -- $i | grep -e '^-[^-]' | grep -v -e '["]\|\.\{3\}\|.*</\?\(Entry\|EntryCount\|ID\)>' | wc | xargs
done
