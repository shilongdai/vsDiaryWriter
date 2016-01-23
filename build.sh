rm -rf build;
mkdir build;
cd build;
echo "copying src files";
cp -rv ../journal journalWindows32;
cp -rv ../journal journalWindows64;
cp -rv ../journal journalLinux32;
cp -rv ../journal journalLinxu64;
cp -rv ../journal journalMac64;
echo "configuring build dependencies";
sed -i -e 's/org.eclipse.swt.gtk.linux.x86_64/org.eclipse.swt.win32.win32.x86/g' journalWindows32/pom.xml;
sed -i -e 's/org.eclipse.swt.gtk.linux.x86_64/org.eclipse.swt.win32.win32.x86_64/g' journalWindows64/pom.xml;
sed -i -e 's/org.eclipse.swt.gtk.linux.x86_64/org.eclipse.swt.gtk.linux.x86/g' journalLinux32/pom.xml;
sed -i -e 's/org.eclipse.swt.gtk.linux.x86_64/org.eclipse.swt.cocoa.macosx.x86_64/g' journalMac64/pom.xml;

echo "starting build";

for i in $(ls); do
	echo "building for $i";
	cd $i;
	mvn install;
	cd target;
	mv ../editor .;
	zip -r $i.zip full-*.jar editor;
	echo "copying product to $1";
	mv $i.zip $1;
	cd ../..;
done;

echo "cleaning up";
cd ..;
rm -rf build;
