module focus::corpus::Download

import IO;
import Prelude;

@javaClass{focus.corpus.GithubDownload}
@reflect
java void downloadGithubRepo(str name, loc url, loc directory, bool force = false);

@javaClass{focus.corpus.GithubDownload}
@reflect{for debugging}
public java map[str,loc] getGithubReposURLs(loc config);
