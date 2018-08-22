module apiFunctionCall::corpus::Download

import IO;
import Prelude;

@javaClass{apiFunctionCall.corpus.GithubDownload}
@reflect
java void downloadGithubRepo(str name, loc url, loc directory, bool force = false);

@javaClass{apiFunctionCall.corpus.GithubDownload}
@reflect{for debugging}
public java map[str,loc] getGithubReposURLs(loc config);
