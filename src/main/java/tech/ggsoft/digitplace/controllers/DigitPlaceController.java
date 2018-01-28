/*
 * Copyright 2018 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package tech.ggsoft.digitplace.controllers;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;

import com.linecorp.bot.model.action.DatetimePickerAction;
import com.linecorp.bot.model.message.template.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.google.common.io.ByteStreams;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.client.MessageContentResponse;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.action.MessageAction;
import com.linecorp.bot.model.action.PostbackAction;
import com.linecorp.bot.model.action.URIAction;
import com.linecorp.bot.model.event.BeaconEvent;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.FollowEvent;
import com.linecorp.bot.model.event.JoinEvent;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.PostbackEvent;
import com.linecorp.bot.model.event.UnfollowEvent;
import com.linecorp.bot.model.event.message.AudioMessageContent;
import com.linecorp.bot.model.event.message.ImageMessageContent;
import com.linecorp.bot.model.event.message.LocationMessageContent;
import com.linecorp.bot.model.event.message.StickerMessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.event.message.VideoMessageContent;
import com.linecorp.bot.model.event.source.GroupSource;
import com.linecorp.bot.model.event.source.RoomSource;
import com.linecorp.bot.model.event.source.Source;
import com.linecorp.bot.model.message.AudioMessage;
import com.linecorp.bot.model.message.ImageMessage;
import com.linecorp.bot.model.message.ImagemapMessage;
import com.linecorp.bot.model.message.LocationMessage;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.StickerMessage;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.VideoMessage;
import com.linecorp.bot.model.message.imagemap.ImagemapArea;
import com.linecorp.bot.model.message.imagemap.ImagemapBaseSize;
import com.linecorp.bot.model.message.imagemap.MessageImagemapAction;
import com.linecorp.bot.model.message.imagemap.URIImagemapAction;
import com.linecorp.bot.model.response.BotApiResponse;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;

import lombok.NonNull;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import tech.ggsoft.digitplace.DigitPlaceApplication;
import tech.ggsoft.digitplace.model.Game;

@Slf4j
@LineMessageHandler
public class DigitPlaceController {

	private String userId;
	private String senderId;

	StringBuilder strb = new StringBuilder();

	private Integer d1;
	private Integer d2;
	private Integer d3;
	private Integer d4;

	private Hashtable<String, Game> games = new Hashtable<>();
	private Game game = new Game();

	@Autowired
	private LineMessagingClient lineMessagingClient;

	@PostConstruct
	public void init() {
		log.info("Initial");
		games = new Hashtable<>();
		game = new Game();
	}

	@EventMapping
	public void handleTextMessageEvent(MessageEvent<TextMessageContent> event) throws Exception {
		// log.info("Handle Text Message");

		TextMessageContent message = event.getMessage();
		handleTextContent(event.getReplyToken(), event, message);
	}

	@EventMapping
	public void handleStickerMessageEvent(MessageEvent<StickerMessageContent> event) {
		// log.info("Handle Sticker Message");
		// handleSticker(event.getReplyToken(), event.getMessage());
	}

	@EventMapping
	public void handleLocationMessageEvent(MessageEvent<LocationMessageContent> event) {
		/*
		 * log.info("Handle Location Message"); LocationMessageContent locationMessage =
		 * event.getMessage(); reply(event.getReplyToken(), new LocationMessage(
		 * locationMessage.getTitle(), locationMessage.getAddress(),
		 * locationMessage.getLatitude(), locationMessage.getLongitude() ));
		 */
	}

	@EventMapping
	public void handleImageMessageEvent(MessageEvent<ImageMessageContent> event) throws IOException {
		// You need to install ImageMagick
		/*
		 * log.info("Handle Image Message"); handleHeavyContent( event.getReplyToken(),
		 * event.getMessage().getId(), responseBody -> { DownloadedContent jpg =
		 * saveContent("jpg", responseBody); DownloadedContent previewImg =
		 * createTempFile("jpg"); system( "convert", "-resize", "240x",
		 * jpg.path.toString(), previewImg.path.toString()); reply(((MessageEvent)
		 * event).getReplyToken(), new ImageMessage(jpg.getUri(), jpg.getUri())); });
		 */
	}

	@EventMapping
	public void handleAudioMessageEvent(MessageEvent<AudioMessageContent> event) throws IOException {
		log.info("Handle Audio Message");
		/*
		 * handleHeavyContent( event.getReplyToken(), event.getMessage().getId(),
		 * responseBody -> { DownloadedContent mp4 = saveContent("mp4", responseBody);
		 * reply(event.getReplyToken(), new AudioMessage(mp4.getUri(), 100)); });
		 */
	}

	@EventMapping
	public void handleVideoMessageEvent(MessageEvent<VideoMessageContent> event) throws IOException {
		// You need to install ffmpeg and ImageMagick.
		/*
		 * log.info("Handle Video Message"); handleHeavyContent( event.getReplyToken(),
		 * event.getMessage().getId(), responseBody -> { DownloadedContent mp4 =
		 * saveContent("mp4", responseBody); DownloadedContent previewImg =
		 * createTempFile("jpg"); system("convert", mp4.path + "[0]",
		 * previewImg.path.toString()); reply(((MessageEvent) event).getReplyToken(),
		 * new VideoMessage(mp4.getUri(), previewImg.uri)); });
		 */
	}

	@EventMapping
	public void handleUnfollowEvent(UnfollowEvent event) {
		log.info("unfollowed this bot: {}", event);
	}

	@EventMapping
	public void handleFollowEvent(FollowEvent event) {
		// String replyToken = event.getReplyToken();
		// this.replyText(replyToken, "Got followed event");
	}

	@EventMapping
	public void handleJoinEvent(JoinEvent event) {
		// String replyToken = event.getReplyToken();
		// this.replyText(replyToken, "Joined " + event.getSource());
	}

	@EventMapping
	public void handlePostbackEvent(PostbackEvent event) {
		String replyToken = event.getReplyToken();
		this.replyText(replyToken, "Got postback data " + event.getPostbackContent().getData() + ", param "
				+ event.getPostbackContent().getParams().toString());
	}

	@EventMapping
	public void handleBeaconEvent(BeaconEvent event) {
		// String replyToken = event.getReplyToken();
		// this.replyText(replyToken, "Got beacon message " +
		// event.getBeacon().getHwid());
	}

	@EventMapping
	public void handleOtherEvent(Event event) {
		log.info("Received message(Ignored): {}", event);
	}

	private void reply(@NonNull String replyToken, @NonNull Message message) {
		reply(replyToken, Collections.singletonList(message));
	}

	private void push(@NonNull String userid, @NonNull Message message) {
		push(userid, Collections.singletonList(message));
	}

	private void reply(@NonNull String replyToken, @NonNull List<Message> messages) {
		try {
			BotApiResponse apiResponse = lineMessagingClient.replyMessage(new ReplyMessage(replyToken, messages)).get();
			log.info("Sent messages: {}", apiResponse);
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

	private void push(@NonNull String userid, @NonNull List<Message> messages) {
		try {
			BotApiResponse apiResponse = lineMessagingClient.pushMessage(new PushMessage(userid, messages)).get();
			log.info("Sent messages: {} to {}", apiResponse, userid);
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

	private void replyText(@NonNull String replyToken, @NonNull String message) {
		if (replyToken.isEmpty()) {
			throw new IllegalArgumentException("replyToken must not be empty");
		}
		if (message.length() > 1000) {
			message = message.substring(0, 1000 - 2) + "……";
		}
		this.reply(replyToken, new TextMessage(message));
	}

	private void pushText(@NonNull String userid, @NonNull String message) {
		if (userid.isEmpty()) {
			throw new IllegalArgumentException("userid must not be empty");
		}
		if (message.length() > 1000) {
			message = message.substring(0, 1000 - 2) + "……";
		}
		this.push(userid, new TextMessage(message));
	}

	private void handleHeavyContent(String replyToken, String messageId,
			Consumer<MessageContentResponse> messageConsumer) {
		final MessageContentResponse response;
		try {
			response = lineMessagingClient.getMessageContent(messageId).get();
		} catch (InterruptedException | ExecutionException e) {
			reply(replyToken, new TextMessage("Cannot get image: " + e.getMessage()));
			throw new RuntimeException(e);
		}
		messageConsumer.accept(response);
	}

	private void handleSticker(String replyToken, StickerMessageContent content) {
		reply(replyToken, new StickerMessage(content.getPackageId(), content.getStickerId()));
	}

	private void handleTextContent(String replyToken, Event event, TextMessageContent content) throws Exception {
		String text = content.getText().toLowerCase();

		log.info("Got text message from {}: {}", replyToken, text);
		switch (text) {
		/*
		 * case "profile": { String userId = event.getSource().getUserId(); if (userId
		 * != null) { lineMessagingClient .getProfile(userId) .whenComplete((profile,
		 * throwable) -> { if (throwable != null) { this.replyText(replyToken,
		 * throwable.getMessage()); return; }
		 * 
		 * this.reply( replyToken, Arrays.asList(new TextMessage( "Display name: " +
		 * profile.getDisplayName()), new TextMessage("Status message: " +
		 * profile.getStatusMessage())) );
		 * 
		 * }); } else { this.replyText(replyToken,
		 * "Bot can't use profile API without user ID"); } break; } case "push": {
		 * log.info("push event!"); String userId = event.getSource().getUserId();
		 * String senderId = event.getSource().getSenderId();
		 * log.info("userid : "+userId); log.info("senderId : "+senderId); if (userId !=
		 * null) { lineMessagingClient .getProfile(userId) .whenComplete((profile,
		 * throwable) -> { if (throwable != null) { this.pushText(userId,
		 * throwable.getMessage()); return; }
		 * 
		 * log.info("push to userid"); this.push( userId, Arrays.asList(new TextMessage(
		 * "Display name: " + profile.getDisplayName()), new
		 * TextMessage("Status message: " + profile.getStatusMessage())) );
		 * log.info("push to senderid"); this.push( senderId, Arrays.asList(new
		 * TextMessage( "Display name: " + profile.getDisplayName()), new
		 * TextMessage("Status message: " + profile.getStatusMessage())) );
		 * 
		 * }); } else { this.replyText(replyToken,
		 * "Bot can't use profile API without user ID"); } break; } case "bye": { Source
		 * source = event.getSource(); if (source instanceof GroupSource) {
		 * this.replyText(replyToken, "Leaving group");
		 * lineMessagingClient.leaveGroup(((GroupSource) source).getGroupId()).get(); }
		 * else if (source instanceof RoomSource) { this.replyText(replyToken,
		 * "Leaving room"); lineMessagingClient.leaveRoom(((RoomSource)
		 * source).getRoomId()).get(); } else { this.replyText(replyToken,
		 * "Bot can't leave from 1:1 chat"); } break; } case "confirm": {
		 * ConfirmTemplate confirmTemplate = new ConfirmTemplate( "Do it?", new
		 * MessageAction("Yes", "Yes!"), new MessageAction("No", "No!") );
		 * TemplateMessage templateMessage = new TemplateMessage("Confirm alt text",
		 * confirmTemplate); this.reply(replyToken, templateMessage); break; } case
		 * "buttons": { String imageUrl = createUri("/static/buttons/1040.jpg");
		 * ButtonsTemplate buttonsTemplate = new ButtonsTemplate( imageUrl,
		 * "My button sample", "Hello, my button", Arrays.asList( new
		 * URIAction("Go to line.me", "https://line.me"), new
		 * PostbackAction("Say hello1", "hello こんにちは"), new PostbackAction("言 hello2",
		 * "hello こんにちは", "hello こんにちは"), new MessageAction("Say message", "Rice=米") ));
		 * TemplateMessage templateMessage = new TemplateMessage("Button alt text",
		 * buttonsTemplate); this.reply(replyToken, templateMessage); break; } case
		 * "carousel": { String imageUrl = createUri("/static/buttons/1040.jpg");
		 * CarouselTemplate carouselTemplate = new CarouselTemplate( Arrays.asList( new
		 * CarouselColumn(imageUrl, "hoge", "fuga", Arrays.asList( new
		 * URIAction("Go to line.me", "https://line.me"), new URIAction("Go to line.me",
		 * "https://line.me"), new PostbackAction("Say hello1", "hello こんにちは") )), new
		 * CarouselColumn(imageUrl, "hoge", "fuga", Arrays.asList( new
		 * PostbackAction("言 hello2", "hello こんにちは", "hello こんにちは"), new
		 * PostbackAction("言 hello2", "hello こんにちは", "hello こんにちは"), new
		 * MessageAction("Say message", "Rice=米") )), new CarouselColumn(imageUrl,
		 * "Datetime Picker", "Please select a date, time or datetime", Arrays.asList(
		 * new DatetimePickerAction("Datetime", "action=sel", "datetime",
		 * "2017-06-18T06:15", "2100-12-31T23:59", "1900-01-01T00:00"), new
		 * DatetimePickerAction("Date", "action=sel&only=date", "date", "2017-06-18",
		 * "2100-12-31", "1900-01-01"), new DatetimePickerAction("Time",
		 * "action=sel&only=time", "time", "06:15", "23:59", "00:00") )) ));
		 * TemplateMessage templateMessage = new TemplateMessage("Carousel alt text",
		 * carouselTemplate); this.reply(replyToken, templateMessage); break; } case
		 * "image_carousel": { String imageUrl = createUri("/static/buttons/1040.jpg");
		 * ImageCarouselTemplate imageCarouselTemplate = new ImageCarouselTemplate(
		 * Arrays.asList( new ImageCarouselColumn(imageUrl, new
		 * URIAction("Goto line.me", "https://line.me") ), new
		 * ImageCarouselColumn(imageUrl, new MessageAction("Say message", "Rice=米") ),
		 * new ImageCarouselColumn(imageUrl, new PostbackAction("言 hello2",
		 * "hello こんにちは", "hello こんにちは") ) )); TemplateMessage templateMessage = new
		 * TemplateMessage("ImageCarousel alt text", imageCarouselTemplate);
		 * this.reply(replyToken, templateMessage); break; } case "imagemap":
		 * this.reply(replyToken, new ImagemapMessage( createUri("/static/rich"),
		 * "This is alt text", new ImagemapBaseSize(1040, 1040), Arrays.asList( new
		 * URIImagemapAction( "https://store.line.me/family/manga/en", new ImagemapArea(
		 * 0, 0, 520, 520 ) ), new URIImagemapAction(
		 * "https://store.line.me/family/music/en", new ImagemapArea( 520, 0, 520, 520 )
		 * ), new URIImagemapAction( "https://store.line.me/family/play/en", new
		 * ImagemapArea( 0, 520, 520, 520 ) ), new MessageImagemapAction( "URANAI!", new
		 * ImagemapArea( 520, 520, 520, 520 ) ) ) )); break;
		 */
		case "g help":
			this.help(replyToken, text);
			break;
		case "g ?":
			this.help(replyToken, text);
			break;
		case "g start":
			// this.help(replyToken, text);
			userId = event.getSource().getUserId();
			senderId = event.getSource().getSenderId();

			d1 = randInt(0, 9);
			d2 = randInt(0, 9);
			d3 = randInt(0, 9);
			d4 = randInt(0, 9);

			strb.setLength(0);
			strb.append(d1);
			strb.append(d2);
			strb.append(d3);
			strb.append(d4);
			game = new Game(senderId, strb.toString(), d1, d2, d3, d4, LocalTime.now());

			games = Optional.ofNullable(games).orElse(new Hashtable<>());

			if (Optional.ofNullable(games.get(senderId)).isPresent()) {
				// Already Start
				games.replace(senderId, game);
			} else {
				games.put(senderId, game);
			}
			this.replyText(replyToken, "Game Start!" + game.getQuest());
			break;
		case "g stop":
			// this.help(replyToken, text);
			games = Optional.ofNullable(games).orElse(new Hashtable<>());
			games.remove(senderId);
			this.replyText(replyToken, "Game Over!");
			break;

		default:
			// log.info("Returns echo message {}: {}", replyToken, text);
			// this.replyText(replyToken, text);
			// Verify game already start
			games = Optional.ofNullable(games).orElse(new Hashtable<>());
			game = games.get(senderId);

			if (game != null) {
				Integer digit = 0;
				Integer place = 0;
				String quest = game.getQuest();
				//List<String> quests = new ArrayList<>(Arrays.asList(a));
				//String quests[] = game.getQuest().split("");
				//String texts[] = text.split("");
				List<String> quests = new ArrayList<>(Arrays.asList(game.getQuest().split("")));
				List<String> texts = new ArrayList<>(Arrays.asList(text.split("")));


				List<Integer> placeUsed = new ArrayList<>();

				// verify integer
				if (text.length() == 4 && isInteger(text)) {
					// Length 4 and Integer
					/*for (Integer i = 0; i < 4; i++) {
						// loop text
						if (text.substring(i, i + 1).equals(quest.substring(i, i + 1))) {
							place++;
							log.info("add place:"+place);
							placeUsed.put(i, i);
							log.info("i"+i+"i"+i+" add placeUsed:"+placeUsed.toString());
						} else {
							for (Integer j = 0; j < 4; j++) {
								// Find in quest
								if (text.substring(i, i + 1).equals(quest.substring(j, j + 1))) {
									if (placeUsed.get(i) == null) {
										// can use
										digit++;
										log.info("add digit:"+digit);
										placeUsed.put(j, j);
										
										log.info("i"+i+"j"+j+" add placeUsed:"+placeUsed.toString());
									}
								}
							}
						}
					} // for i					
					*/
					//place find
					for (Integer i = 0; i < 4; i++) {						
						if (texts.get(i).equals(quests.get(i))) {
							place++;
							//log.info("add place:"+place);
							placeUsed.add(i);
							log.info("i"+i+"i"+i+" add placeUsed:"+placeUsed.toString());
						} 
					}
					//remove place use quest 
					for(Integer j: placeUsed) {
						log.info("remove j: "+j);
						texts.remove(j);
						quests.remove(j);
					}
					log.info("texts: "+texts.toString());
					log.info("quests: "+quests.toString());
					
					//find digit
					for(Integer k=0; k < texts.size(); k++) {
						int s = quests.size();
						log.info("quests.size():"+s);
						//int l=0;
						while(s>0) {
							if (texts.get(k).equals(quests.get(s-1))){
								digit++;
								log.info("add digit:"+digit);
								quests.remove(s-1);
								
								break;
							}
							//l++;
							s--;
						}
						/*for(Integer l=0; l< quests.size(); l++) {
							if (texts.get(k).equals(quests.get(l))) {
								digit++;
								log.info("add digit:"+digit);
								quests.remove(l);
								log.info("quests.size():"+quests.size());
								break;
							}
						}*/
					}
					
					if (place >= 4) {
						games.remove(senderId);
						lineMessagingClient.getProfile(userId).whenComplete((profile, throwable) -> {
							if (throwable != null) {
								this.replyText(replyToken, "Your Win!");
								return;
							}
							this.replyText(replyToken, profile.getDisplayName() + " Win!");
						});

					} else {
						strb.setLength(0);
						strb.append("ถูกต้อง ตัวเลข ");
						strb.append(digit);
						strb.append(" ตำแหน่ง ");
						strb.append(place);
						this.replyText(replyToken, strb.toString());
					}
				} // Game verify
			} // game

			break;
		}
	}

	private int randInt(int min, int max) {
		// Usually this can be a field rather than a method variable
		Random rand = new Random();
		// nextInt is normally exclusive of the top value,
		// so add 1 to make it inclusive
		int randomNum = rand.nextInt((max - min) + 1) + min;

		return randomNum;
	}

	private void help(@NonNull String replyToken, @NonNull String message) {
		if (replyToken.isEmpty()) {
			throw new IllegalArgumentException("replyToken must not be empty");
		}

		StringBuilder strb = new StringBuilder();

		strb.append("วิธีเล่น\n");
		strb.append("1. พิมพ์ g start เพื่อเริ่มเกมส์\n");
		strb.append("	- ระบบจะสุ่มเลือกตัวเลข 0-9 มาจำนวน 4 หลัก เช่น 1111 หรือ 1150\n");
		strb.append("2. พิมพ์ตัวเลข 4 หลัก เช่น 1234  เพื่อทายตัวเลข\n");
		strb.append("	- ระบบจะแสดง จำนวน ตัวเลข และ ตำแหน่ง ที่ถูกต้อง เช่น ตัวเลข : 1 ตำแหน่ง : 3\n");
		strb.append("3. เกมส์จบเมือทายถูกครบ 4 ตำแหน่ง\n");
		strb.append("4. สามารถจบเกมส์ได้ทันที โดยการพิมพ์ g stop\n");
		message = strb.toString();
		this.reply(replyToken, new TextMessage(message));
	}

	private static String createUri(String path) {
		return ServletUriComponentsBuilder.fromCurrentContextPath().path(path).build().toUriString();
	}

	private void system(String... args) {
		ProcessBuilder processBuilder = new ProcessBuilder(args);
		try {
			Process start = processBuilder.start();
			int i = start.waitFor();
			log.info("result: {} =>  {}", Arrays.toString(args), i);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		} catch (InterruptedException e) {
			log.info("Interrupted", e);
			Thread.currentThread().interrupt();
		}
	}

	private static DownloadedContent saveContent(String ext, MessageContentResponse responseBody) {
		log.info("Got content-type: {}", responseBody);

		DownloadedContent tempFile = createTempFile(ext);
		try (OutputStream outputStream = Files.newOutputStream(tempFile.path)) {
			ByteStreams.copy(responseBody.getStream(), outputStream);
			log.info("Saved {}: {}", ext, tempFile);
			return tempFile;
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private static DownloadedContent createTempFile(String ext) {
		String fileName = LocalDateTime.now().toString() + '-' + UUID.randomUUID().toString() + '.' + ext;
		Path tempFile = DigitPlaceApplication.downloadedContentDir.resolve(fileName);
		tempFile.toFile().deleteOnExit();
		return new DownloadedContent(tempFile, createUri("/downloaded/" + tempFile.getFileName()));
	}

	@Value
	public static class DownloadedContent {
		Path path;
		String uri;
	}

	public static boolean isInteger(String s) {
		log.info("Begin");
		try {
			if (Integer.parseInt(s) < 0)
				return false;
		} catch (NumberFormatException e) {
			return false;
		} catch (NullPointerException e) {
			return false;
		}
		// only got here if we didn't return false
		log.info("End");
		return true;
	}

}
