# Integration notes

This folder combines:

- `jGraphed2004.zip`: modernized repository wrapper/build files, README, jar, and source list
- `flipsav.zip/flip`: full source tree and resources for the Open/extended variant

## Chosen active base

The active source tree is taken from `flipsav.zip/flip` because it contains the complete Java packages:

- `dataStructure/`
- `graphException/`
- `graphStructure/`
- `operation/`
- `userInterface/`
- `images/`
- `help/`

## Preserved from the 2004-modernized zip

The following root-level repository files were copied in from `jGraphed2004.zip`:

- `.gitignore`
- `build.bat`
- `run.bat`
- `manifest.txt`
- `README.md`
- `README_old.md`
- `fix_legacy_java_identifiers.ps1`
- `merge.bat`
- `push.bat`
- `sources.txt`

## Launcher decision

Two root launcher files overlap between the zips:

- `JGraphEdApplet.java`
- `JGraphEdFrame.java`

The active files are the versions from `flipsav.zip/flip`.
Reason: they align with the extended/Open source tree and include the larger frame size used there.

The versions from `jGraphed2004.zip` are preserved under:

- `integration/legacy_2004_wrappers/`

## Excluded from the integrated tree

These were intentionally not placed in the active tree:

- `flip/bin/` compiled `.class` output
- Eclipse metadata: `.classpath`, `.project`, `.settings/`
- root `JGraphEd.jar` from `jGraphed2004.zip` (kept out of source tree; better suited for GitHub Releases)

## Suggested Git layout

- Keep this integrated tree as the initial contents of a new repository, e.g. `JGraphEd-OpenGraph`
- Use `main` for stable snapshots
- Use `dev` for ongoing integration/refactoring
- Put built jars in GitHub Releases, not in git history
