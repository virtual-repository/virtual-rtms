package org.virtual.rtms;

import static java.lang.System.*;
import static java.nio.file.StandardCopyOption.*;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.xfer.FileSystemFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.virtualrepository.Asset;

@Singleton
public class BasePublisher {

	private static Logger log = LoggerFactory.getLogger(BasePublisher.class);

	private final Provider<SSHClient> clients;
	private final Configuration configuration;
	
	@Inject
	public BasePublisher(Provider<SSHClient> clients,Configuration configuration) {
		this.clients = clients;
		this.configuration=configuration;
	}

	void publish(Asset asset, InputStream stream, String suffix) {
	
		
		try (SSHClient client = clients.get()) {
			
			long time = currentTimeMillis();
		
			log.info("uploading codelist {} to rtms",asset.id());
			
			File file = toTmpFile(asset.name(), suffix, stream);
			
			client.newSCPFileTransfer().upload(new FileSystemFile(file),configuration.publishPath());

			log.info("uploaded codelist {} to rtms in {} ms. ",asset.id(),currentTimeMillis()-time);

			
		}
		catch(Exception e) {
			throw new RuntimeException("cannot publish asset "+asset.id()+" (see cause)",e);
		}

		

		
	}

	private File toTmpFile(String name, String suffix, InputStream stream) throws Exception {

		File file = File.createTempFile(name + "-", ".upload");

		try (InputStream is = stream) {

			Files.copy(stream, Paths.get(file.toURI()), REPLACE_EXISTING);

		} catch (Exception e) {
			throw new RuntimeException("cannot crate local file " + name + " to upload ", e);
		}

		return file;

	}
}
