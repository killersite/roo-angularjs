roo-angularjs
=============

Spring Roo add-on to create and manage Spring MVC actions with AngularJS views.

NOTE: Move along... nothing to see here yet...

-------------

how to build
Roo now uses GPG to automatically sign build outputs. If you haven’t installed GPG, download and install it: http://www.gnupg.org/download/

Ensure you have a valid signature. Use “gpg –list-secret-keys”. You should see some output like this:

$ gpg –list-secret-keys
/home/balex/.gnupg/secring.gpg
——————————
sec 1024D/00B5050F 2009-03-28
uid Ben Alex 
uid Ben Alex 
uid Ben Alex 
ssb 4096g/2DB6833B 2009-03-28

If you don’t see the output, it means you first need to create a key. It’s very easy to do this. Just use “gpg –gen-key”. Then verify your newly-created key was indeed created: “gpg –list-secret-keys”.

Next you need to publish your key to a public keyserver. Take a note of the “sec” key ID shown from the –list-secret-keys. In my case it’s key ID “00B5050F”. Push your public key to a keyserver via the command
“gpg –keyserver hkp://pgp.mit.edu –send-keys 00B5050F” (of course changing the key ID at the end). Most public key servers share keys, so you don’t need to send your public key to multiple key servers.

Finally, every time you build you will be prompted for the password of your key. 
Edit ~/.bashrc and add -Dgpg.passphrase=thephrase to MAVEN_OPTS

One final note if you’re new to GPG: don’t lose your private key! Backup the secring.gpg file, as you’ll need it to ever revoke your key or sign a replacement key 