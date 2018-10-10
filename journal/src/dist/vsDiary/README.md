# VSDiaryWriter
## Overview
The VSDiaryWriter is a secure, modular journal application that enables the users to write diary entries. The application was designed with cryptography and security in mind from the beginning, supporting AEAD encryption, symmetrical encryption, HMAC, and symmetrical encryption with both block and stream ciphers. By default, the authentication uses very slow BCrypt password hashing algorithm to guarantee a long brute-force cracking time. The VSDiaryWriter application also supports WYSIWYG editing via the CKeditor, ensuring a pleasant user editing experience.

## Status
Currently, the application is production ready with a few glitches in the user interface that needs to be fixed. There are planned extensions to the application to make it support third party extensions.

## Usage
The user interface is fairly straightforward. If it is the first time you ran this application, it will prompt you to set a password. Otherwise, an unlock prompt will be displayed. When that occurs, enter your password and press the login button to unlock. The password hashing should take a moment. Then, if successful, the main window of the application should be displayed. The list on the left shows the list of entries, while the section on the right allows you to compose your entry.

To create an entry, press the new button, and then write your entry with the WYSIWYG editor on the right. After you are done, press the save button to save your progress.

To remove an entry, select the appropriate entry from the list of entries, and press delete.

To edit an entry, select the entry from the list and then modify the entry with the WYSIWYG editor. Press save after you are done.

To view an entry, select the appropriate entry from the list.

To search an entry by keywords, type in the keywords in the search bar on top of the entry list.

To change the password, press the change password button from the settings menu, and change the password using the prompt.

### Configuration
There are many configuration options available that changes the way entries are transformed. For normal users, the default options are sufficient. However, if you are an advanced user or have special needs, you can press the options button on the button bar. A configuration window should pop up. Then, adjust the settings accordingly. After a change to the settings is made, the previous entries are unaffected. To re-encrypt all the previous entries according to the current settings, press the re-encrypt button.

## Details
### Structure
The main components of the VSDiaryWriter includes:

The journal database
The transformation chain
The authenticator
The journal database is responsible for persisting the entries and associated meta information. Currently, the H2 database is used as the backend. The scheme used for creating all the tables can be found here: https://github.com/shilongdai/vsDiaryWriter/blob/master/journal/src/main/resources/create.sql

The transformation chain is a chain of processors that are responsible for transforming the journal entries before saving to database and after retrieval from database. Each processor is responsible for creating and maintaining meta-information for its own use. The datastructure of the chain and the format of the meta-information is specialized towards storing encryption/cryptography related information such as key or IV. However, other types of transformation, such as compression, can also be done in the framework.

The authenticator is the component that verifies and maintains the credentials of the user. Currently, the authentication is performed via password.

### Cryptographic Strength
Authentication
The password hashing algorithm used by this application is the BCrypt algorithm, a reliable password hashing algorithm that has been adopted and used in the past decade by the industry. The parameters chosen for BCrypt takes several seconds on a high end PC, which is sufficient to dis-encourage any attackers from brute-forcing or building a table. You can read more about BCrypt here: https://auth0.com/blog/hashing-in-action-understanding-bcrypt/

#### Key Generation
The password of the user is not directly used for authentication because the average password has very low entropy (i.e. it is not uncommon to see things like pass12345). When a password is entered, a key encrypting key is generated from the password using the PKCS12 KDF algorithm with a randomly generated salt. Then, in order to further guarantee high key entropy with weak password and ensure the strength of encryption with possibly large amount of data as multimedia content, a random key is generated and stored for each entry encrypted by the key encrypting key. This ensures that the key used for encrypting the bulk of the data is strong with high entropy even with a weak password. It also guarantees that if some cipher/modes with period are used, it is almost impossible for the period to be reached for any entries.

#### Encryption
The encryption process is quite conventional. The journal entries are transformed from plain text to cipher text based on the processor modules enabled. Currently, there are compression module, AEAD modes encryption module, stream cipher encryption module, and HMAC module. The entries would be processed sequentially based on the order of the modules in the configuration.
