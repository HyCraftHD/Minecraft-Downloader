# Minecraft Downloader

This library lets you download minecraft files and run minecraft with it afterwards when you authenticate with the [Minecraft Authenticator](https://github.com/HyCraftHD/Minecraft-Authenticator).
Its only console based with command line arguments and is not a fully functional minecraft launcher. The main goal is to download minecraft files like jars, assets, natives and get information about them.
The only api is the main method with its arguments. Everything else is not public api and can change at any time.

# Building

To build just run ``./gradlew build``. You will find the jars in the build/libs directory.
This project requires gson, logging_util, log4j-iostreams, minecraft_authenticator and jopt-simple as dependencies.

# Include in your own project

To include this project you can use the maven build of this project which will resolve all required dependencies automatically.
The latest version is the latest tag in github.

```gradle
repositories {
	maven {
		url = "https://repo.u-team.info"
	}
}

dependencies {
	implementation "net.hycrafthd:minecraft_downloader:XYZ"
}
```

# Usage

Find all options (main class arguments)

```
--help
```

Basic run with authentication (main class arguments)

```
--version 1.16.5 --output download --launch --run game --auth-file auth.json --authenticate console
```

Basic run that use and existing authentication file (main class arguments)

```
--version 1.16.5 --output download --launch --run game --auth-file auth.json
```

Only download jars and print information

```
--version 1.16.5 --output download --skip-assets --extra-information --library-list libraries.txt --library-list-natives native-libraries.txt
```

# License

This project is licensed under apache 2 license. For more information see [here](LICENSE).
