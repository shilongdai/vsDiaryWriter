#!/bin/bash
rm -rf build;
mkdir build;
cd build;
echo "copying src files";
cp -rv ../journal journalWindows32;
cp -rv ../journal journalLinux32;
cp -rv ../journal journalLinux64;
cp -rv ../journal journalMac64;

echo "copying additional files";
for i in $(ls); do
	cp -rv ../editor $i;
	cp -rv ../i18n $i;
	cp -rv ../config $i;
	cp -rv ../data.mv.db $i;
done;
echo "configuring build dependencies";
sed -i -e 's/org.eclipse.swt.gtk.linux.x86_64/org.eclipse.swt.win32.win32.x86/g' journalWindows32/pom.xml;
sed -i -e 's/org.eclipse.swt.gtk.linux.x86_64/org.eclipse.swt.gtk.linux.x86/g' journalLinux32/pom.xml;
sed -i -e 's/org.eclipse.swt.gtk.linux.x86_64/org.eclipse.swt.cocoa.macosx.x86_64/g' journalMac64/pom.xml;

xulRunner[3]="xulrunner-24.0.en-US.win32.zip";
mozilla="https://ftp.mozilla.org/pub/xulrunner/releases/24.0/runtimes/"

echo "starting build";
for i in $(ls); do
	echo "building for $i";
	
	#run maven
	cd $i;
	mvn install;

	#copy editor
	cd target;
	mv ../editor .;
	#copy i18n
	mv ../i18n .;
	cp ../../../LICENSE .

	mv ../data.mv.db .;
	mv ../config .;

	#get XULRunner
	if [ $i = "journalWindows32" ]; then
		wget $mozilla${xulRunner[3]};
		unzip ${xulRunner[3]};
		zip -r $i.zip full-journal*.jar editor xulrunner $(ls | grep launcher.sh) LICENSE i18n config data.mv.db;
	fi

	if [ $i = "journalLinux32" ]; then
		cp ../../../linux-launcher.sh .
		tar -cJvf $i.tar.xz full-journal*.jar editor $(ls | grep launcher.sh) LICENSE i18n config data.mv.db;
	fi

	if [ $i = "journalLinux64" ]; then
		cp ../../../linux-launcher.sh .
		tar -cJvf $i.tar.xz full-journal*.jar editor $(ls | grep launcher.sh) LICENSE i18n config data.mv.db;
	fi

	if [ $i = "journalMac64" ]; then
		cp ../../../mac-launcher.sh .
		tar -cJvf $i.tar.xz full-journal*.jar editor $(ls | grep launcher.sh) LICENSE i18n config data.mv.db;
	fi
	
	echo "copying product to $1";
	mv $(ls | grep $i) $1;
	cd ../..;
done;

echo "cleaning up";
cd ..;
rm -rf build;
