package focus.corpus;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.rascalmpl.interpreter.IEvaluatorContext;
import org.rascalmpl.uri.URIResolverRegistry;

import io.usethesource.vallang.IBool;
import io.usethesource.vallang.IMap;
import io.usethesource.vallang.IMapWriter;
import io.usethesource.vallang.ISourceLocation;
import io.usethesource.vallang.IString;
import io.usethesource.vallang.IValueFactory;

public class GithubDownload {
	
	public final static String NUMBER_REPOS = "repos";
	public final static String REPO_PREFIX = "repo.";
	public final static String REPO_INFO_SEPARATOR = ";";
	
	private final IValueFactory VF;
	private final URIResolverRegistry registry = URIResolverRegistry.getInstance();
	
	public GithubDownload(IValueFactory vf) {
		this.VF = vf;
	}
	
	public void downloadGithubRepo(IString name, ISourceLocation url, ISourceLocation directory, IBool force, IEvaluatorContext ctx) throws GithubException {
		try {
			File localDir = new File(directory.getPath() + "/" + name.getValue().toLowerCase());
			
			if(force.getValue() && localDir.exists()) {
				FileUtils.deleteDirectory(localDir);
			}
			if(!localDir.exists()) {
				localDir.mkdirs();
			}
			if(localDir.list().length == 0) {
				ctx.getStdOut().println("Cloning " + name.getValue() + " to " + directory);
				Git git = Git.cloneRepository()
				.setURI(url.getURI().toString())
				.setDirectory(localDir)
				.call();
				git.close();
			}
			else {
				ctx.getStdOut().println(name.getValue() + " repository was already cloned. Skipping.");
			}
		} 
		catch (GitAPIException e) {
			throw new GithubException("Cannot download " + name + "repository from " + url + ":", e);
		} 
		catch (IOException e) {
			throw new GithubException("Cannot delete existing directory: " +  directory, e);
		} 
	}
	
	public IMap getGithubReposURLs(ISourceLocation config, IEvaluatorContext ctx) throws GithubException {
		try {
			IMapWriter w = VF.mapWriter();
			Properties prop = new Properties();
			InputStream is = registry.getInputStream(config);
			prop.load(is);
			
			int repos = Integer.parseInt(prop.getProperty(NUMBER_REPOS));
			for(int i = 0; i < repos; i++) {
				String repo = prop.getProperty(REPO_PREFIX + i);
				String[] info = repo.split(REPO_INFO_SEPARATOR);
				
				// Two values are expected: repoName ; repoURL
				w.put(VF.string(info[0]), VF.sourceLocation(URI.create(info[1])));
			}
			
			return w.done();
		} 
		catch (IOException e) {
			throw new GithubException("Cannot get GitHub repositories' URLs from " + config.toString() + ":", e);
		}
	}
}
