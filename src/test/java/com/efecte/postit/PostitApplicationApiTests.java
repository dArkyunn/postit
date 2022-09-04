package com.efecte.postit;

import com.efecte.postit.model.Note;
import com.efecte.postit.repository.UnitTestNoteRepository;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureEmbeddedDatabase(provider = ZONKY)
class PostitApplicationApiTests {

	@LocalServerPort
	private int port;
	@Autowired
	private UnitTestNoteRepository mockNoteRepository;

	@Autowired
	private NoteController noteController;

	private static <T> T retrieveResponseFromEntity(@NotNull HttpResponse httpResponse, Class<T> javaClass) throws IOException {
		String jsonFromResponse = EntityUtils.toString(httpResponse.getEntity());
		ObjectMapper mapper = new ObjectMapper()
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return mapper.readValue(jsonFromResponse, javaClass);
	}

	@BeforeEach
	public void setUp() {
		for (int i = 1; i <= 5; i++) {
			Note note = new Note();
			note.setText(String.valueOf(i));

			this.mockNoteRepository.save(note);
		}

		System.out.println("setUp");
	}

	@AfterEach
	public void cleanUp() {
		this.mockNoteRepository.truncate();

		System.out.println("cleanUp");
	}

	@Test
	void controllerLoads() {
		assertThat(noteController).isNotNull();
	}

	@Test
	void repositoryLoads() {
		assertThat(mockNoteRepository).isNotNull();
	}

	//region GetAll Tests

	@Test
	void GetAll_ShouldReturnOKStatusAndEmptyList() throws ClientProtocolException, IOException {
		this.mockNoteRepository.truncate();

		HttpUriRequest request = new HttpGet( "http://localhost:" + port + "/api/notes" );

		HttpResponse httpResponse = HttpClientBuilder.create().build().execute( request );

		assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.OK.value());

		List<Note> notes = Arrays.asList(retrieveResponseFromEntity(httpResponse, Note[].class));

		assertThat((long) notes.size()).isEqualTo(0);
	}

	@Test
	void GetAll_ShouldReturnOKStatusAndFiveElements() throws ClientProtocolException, IOException {
		HttpUriRequest request = new HttpGet( "http://localhost:" + port + "/api/notes" );

		HttpResponse httpResponse = HttpClientBuilder.create().build().execute( request );

		assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.OK.value());

		List<Note> notes = Arrays.asList(retrieveResponseFromEntity(httpResponse, Note[].class));

		assertThat((long) notes.size()).isEqualTo(5);
	}

	//endregion

	//region Get Tests

	@Test
	void Get_ShouldReturnOKStatusAndNote() throws ClientProtocolException, IOException {
		HttpUriRequest request = new HttpGet( "http://localhost:" + port + "/api/notes/" + 1L );

		HttpResponse httpResponse = HttpClientBuilder.create().build().execute( request );

		assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.OK.value());

		Note note = retrieveResponseFromEntity(httpResponse, Note.class);

		assertThat(note).isNotNull();
	}

	@Test
	void Get_ShouldReturnOKStatusAndNoteWithValueOf3() throws ClientProtocolException, IOException {
		HttpUriRequest request = new HttpGet( "http://localhost:" + port + "/api/notes/" + 3L );

		HttpResponse httpResponse = HttpClientBuilder.create().build().execute( request );

		assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.OK.value());

		Note note = retrieveResponseFromEntity(httpResponse, Note.class);

		assertThat(note).isNotNull();
		assertThat(note.getText()).isEqualTo("3");
	}

	@Test
	void Get_ShouldReturnNotFoundStatus() throws ClientProtocolException, IOException {
		HttpUriRequest request = new HttpGet( "http://localhost:" + port + "/api/notes/" + 6 );

		HttpResponse httpResponse = HttpClientBuilder.create().build().execute( request );

		assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
	}

	//endregion

	//region Post Tests

	@Test
	void Post_ShouldAddNewNote() throws ClientProtocolException, IOException {
		HttpPost request = new HttpPost( "http://localhost:" + port + "/api/notes/" );

		String json = "{\"text\":\"test value\"}";
		StringEntity entity = new StringEntity(json);
		request.setEntity(entity);
		request.setHeader("Accept", "application/json");
		request.setHeader("Content-type", "application/json");

		HttpResponse httpResponse = HttpClientBuilder.create().build().execute( request );

		assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.OK.value());

		assertThat(this.mockNoteRepository.count()).isEqualTo(6);
	}

	@Test
	void Post_ShouldIgnoreExplicitIdValueAndAddNewNote() throws ClientProtocolException, IOException {
		HttpPost request = new HttpPost( "http://localhost:" + port + "/api/notes/" );

		String json = "{\"id\":215,\"text\":\"test value\"}";
		StringEntity entity = new StringEntity(json);
		request.setEntity(entity);
		request.setHeader("Accept", "application/json");
		request.setHeader("Content-type", "application/json");

		HttpResponse httpResponse = HttpClientBuilder.create().build().execute( request );

		assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.OK.value());

		Note nullNote = this.mockNoteRepository.findById(215L).orElse(null);

		assertThat(nullNote).isNull();

		Note note = this.mockNoteRepository.findById(6L).orElse(null);

		assertThat(note).isNotNull();
	}

	@Test
	void Post_ShouldAddNewNoteWithTextLengthOf200() throws ClientProtocolException, IOException {
		HttpPost request = new HttpPost( "http://localhost:" + port + "/api/notes/" );

		String testString = StringUtils.repeat("A", 200);
		String json = "{\"id\":215,\"text\":\"" + testString + "\"}";
		StringEntity entity = new StringEntity(json);
		request.setEntity(entity);
		request.setHeader("Accept", "application/json");
		request.setHeader("Content-type", "application/json");

		HttpResponse httpResponse = HttpClientBuilder.create().build().execute( request );

		assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.OK.value());

		assertThat(this.mockNoteRepository.count()).isEqualTo(6);
	}

	@Test
	void Post_ShouldNotAddNewNoteWithTextLengthOfOver200() throws ClientProtocolException, IOException {
		HttpPost request = new HttpPost( "http://localhost:" + port + "/api/notes/" );

		String testString = StringUtils.repeat("A", 201);
		String json = "{\"id\":215,\"text\":\"" + testString + "\"}";
		StringEntity entity = new StringEntity(json);
		request.setEntity(entity);
		request.setHeader("Accept", "application/json");
		request.setHeader("Content-type", "application/json");

		HttpResponse httpResponse = HttpClientBuilder.create().build().execute( request );

		assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED.value());

		assertThat(this.mockNoteRepository.count()).isEqualTo(5);
	}

	@Test
	void Post_ShouldReturnMethodNotAllowedStatusWhenNoEntityIsPassed() throws ClientProtocolException, IOException {
		HttpPost request = new HttpPost( "http://localhost:" + port + "/api/notes" );

		request.setHeader("Accept", "application/json");
		request.setHeader("Content-type", "application/json");

		HttpResponse httpResponse = HttpClientBuilder.create().build().execute( request );

		assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED.value());

		assertThat(this.mockNoteRepository.count()).isEqualTo(5);
	}

	//endregion

	//region Put Tests

	@Test
	void Put_ShouldReturnMethodNotAllowedStatusDueToNoIdPassed() throws ClientProtocolException, IOException {
		HttpPut request = new HttpPut( "http://localhost:" + port + "/api/notes" );

		String json = "{\"text\":\"test value\"}";
		StringEntity entity = new StringEntity(json);
		request.setEntity(entity);
		request.setHeader("Accept", "application/json");
		request.setHeader("Content-type", "application/json");

		HttpResponse httpResponse = HttpClientBuilder.create().build().execute( request );

		assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED.value());

		assertThat(this.mockNoteRepository.count()).isEqualTo(5);
	}

	@Test
	void Put_ShouldReturnNotFoundStatusForNonExistentIdPassed() throws ClientProtocolException, IOException {
		HttpPut request = new HttpPut( "http://localhost:" + port + "/api/notes" );

		String json = "{\"id\":6,\"text\":\"test value\"}";
		StringEntity entity = new StringEntity(json);
		request.setEntity(entity);
		request.setHeader("Accept", "application/json");
		request.setHeader("Content-type", "application/json");

		HttpResponse httpResponse = HttpClientBuilder.create().build().execute( request );

		assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());

		assertThat(this.mockNoteRepository.count()).isEqualTo(5);
	}

	@Test
	void Put_ShouldReturnOKStatusAndUpdateNoteValue() throws ClientProtocolException, IOException {
		HttpPut request = new HttpPut( "http://localhost:" + port + "/api/notes" );

		String json = "{\"id\":1,\"text\":\"test value\"}";
		StringEntity entity = new StringEntity(json);
		request.setEntity(entity);
		request.setHeader("Accept", "application/json");
		request.setHeader("Content-type", "application/json");

		HttpResponse httpResponse = HttpClientBuilder.create().build().execute( request );

		assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.OK.value());

		assertThat(this.mockNoteRepository.count()).isEqualTo(5);

		Note note = this.mockNoteRepository.findById(1L).orElse(null);

		assertThat(note).isNotNull();

		assertThat(note.getText()).isEqualTo("test value");
	}

	//endregion

	//region Delete Tests

	@Test
	void Delete_ShouldReturnOKStatusAndDeleteNote() throws ClientProtocolException, IOException {
		HttpDelete request = new HttpDelete( "http://localhost:" + port + "/api/notes/" + 1L );

		HttpResponse httpResponse = HttpClientBuilder.create().build().execute( request );

		assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.OK.value());

		assertThat(this.mockNoteRepository.count()).isEqualTo(4);

		Note note = this.mockNoteRepository.findById(1L).orElse(null);

		assertThat(note).isNull();
	}

	@Test
	void Delete_ShouldReturnNotFoundStatusForNonExistentId() throws ClientProtocolException, IOException {
		HttpDelete request = new HttpDelete( "http://localhost:" + port + "/api/notes/" + 6L );

		HttpResponse httpResponse = HttpClientBuilder.create().build().execute( request );

		assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());

		assertThat(this.mockNoteRepository.count()).isEqualTo(5);
	}

	//endregion

}
