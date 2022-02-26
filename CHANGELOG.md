# Changelog
All notable changes to this project will be documented in this file.

## [1.0.1] - 2022-02-26
### Changed
 - Updated to java 17
 - Fix that not all minecraft versions are working
 - Fixed a bug that caused multiple classpath entries of the same file
 - Use updated version of authentication library to better support microsoft authentication
 - Generate jar file with the classpath to circumvent a path length issue
 - Use a custom logger file by default now, Added command line option to still use the one from minecraft
 
### Added
 - Added jre downloader
 - Implemented many more command line options. See them with --help

## [1.0.0] - 2021-05-21
### Added
 - Implemented basic downloading of minecraft files and launching the game (with authentication only)
 - The only api is the main method with its arguments. Everything else is not public api and can be changed at any time.