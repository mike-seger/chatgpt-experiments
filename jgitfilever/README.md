

```
diff -y --suppress-common-lines \
	<(git show 96daef9dda8540644d9393bd5a821676dab00954:file.txt) \
	<(git show b8f8e8ebf34c65246577b3651affa0535da78fcb:file.txt) \
	| wc -l
```

```
prev=x
git reflog |cut -b1-7 | while read f ; do 
	if [ "$prev" != "x" ] ; then  
		echo -n "$f "; diff -y --suppress-common-lines \
		<(git show $prev:file.txt) <(git show $f:file.txt) \
		| wc -l
	fi
	prev=$f
done
```
