# Files

[![Build Java](https://github.com/peavers/autotools/actions/workflows/build-java.yml/badge.svg)](https://github.com/peavers/autotools/actions/workflows/build-java.yml)
[![Build Typescript](https://github.com/peavers/autotools/actions/workflows/build-typescript.yml/badge.svg)](https://github.com/peavers/autotools/actions/workflows/build-typescript.yml)
[![Release](https://github.com/peavers/autotools/actions/workflows/release.yml/badge.svg)](https://github.com/peavers/autotools/actions/workflows/release.yml)

A very simple set of autotools for managing files and stuff

## Getting started

```yaml
version: "3"
services:

  # https://github.com/peavers/autotools
  autotools:
    container_name: autotools
    image: peavers/autotools:latest
    restart: unless-stopped
    ports:
      - 8282:8080
    environment:
      - TZ=${TZ}
      - PGID=${PGID}
      - PUID=${PUID}
    volumes:
      - /downloads:/downloads
      - autotools-data:/.data
    logging:
      options:
        max-size: "2m"
        max-file: "5"

volumes:
  autotools-data:
```

Once up and running, head to `http://localhost:8282` for the UI to configure new jobs.

## Jobs

**Careful** no warning or decision is given before files are deleted with jobs.

### Auto unrar

Unrar files from one directory into another. Perfect for those multi-rar downloads. Will skip if the file is found
already in the target directory.

### Copy media files

Does what it says, copies media files from a source into target. Will skip if a file with the same name and extension
already exists in the target directory.

### Delete empty directories

Deletes directories that have no children.

### Delete files less than a specific size

Delete files that are less than the size given. Great for cleaning up `nfo` and `sfv` files for example.

### Delete files which include a text string in their name

Delete files if they have a specific word in their name. Something like test-movie-sample.mp4 and you want to delete all
the samples.

### Delete duplicate media files (basic)

Does a basic check if two media files are identical based on an exact byte matching of the filesize. If identical files
are found, the file with the last access time is kept, all others are deleted.

No warning or decision is given before the files are deleted, so make sure this does what you're expecting.

### Delete duplicate media files (advance)

Works similarly to the basic job but does a more in-depth check for duplicates. A screenshot is taken from all media
files. Should these screenshots be identical than we assume the file is identical.
