package com.spring.chat_app.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatRepo chatRepo;

    // produces = MediaType.TEXT_EVENT_STREAM_VALUE : SSE 프로토콜
    @CrossOrigin
    @GetMapping(value = "/sender/{sender}/receiver/{receiver}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Chat> getMessage(   //Flux(흐름): 계속해서 받는다, response를 유지하면서 데이터를 계속 흘려보낸다.
            @PathVariable String sender,
            @PathVariable String receiver
    ){
        System.out.println("=====getMessage=======");
        return chatRepo.mFindBySender(sender, receiver)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @CrossOrigin
    @PostMapping("/chat")
    public Mono<Chat> setMsg(@RequestBody Chat chat){   //Mono : 한번만 리턴
        System.out.println("=====setMsg=======");
        chat.setCreatedAt(LocalDateTime.now());
        return chatRepo.save(chat); // Object를 리턴하면 자동으로 JSON 변환 (MessageConverter)
    }

    @CrossOrigin
    @GetMapping(value = "/chat/roomNum/{roomNum}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Chat> findByRoomNum(@PathVariable Integer roomNum) {
        System.out.println("=====findByRoomNum=======");
        return chatRepo.mFindByRoomNum(roomNum)
                .subscribeOn(Schedulers.boundedElastic());
    }
}
