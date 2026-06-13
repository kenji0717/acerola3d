#!/bin/sh

rm -f a3format.txt.xhtml

mm2xhtml a3format.txt || exit 1

scp a3format.txt.xhtml ksaito0717@shell.sourceforge.jp:/home/groups/a/ac/acerola3d/htdocs/docs/format/definition/index.html || exit 1

scp catalog.xsd ksaito0717@shell.sourceforge.jp:/home/groups/a/ac/acerola3d/htdocs/docs/format/definition/catalog.xsd.txt || exit 1

rm a3format.txt.xhtml

echo DONE!!!!!!

