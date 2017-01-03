package com.howe.learn.serialization.protostuff;

import com.howe.learn.serialization.protostuff.dto.Author;
import com.howe.learn.serialization.protostuff.dto.Book;
import org.junit.Test;

import java.math.BigDecimal;

/**
 * @Author Karl
 * @Date 2017/1/3 14:30
 */
public class ProtostuffSerializeUtilTest {

    private Book createBook(){
        Book book = new Book();
        book.setTitle("代码大全");
        book.setPrice(BigDecimal.valueOf(99.99));
        Author author = new Author();
        author.setName("史蒂夫·迈克康奈尔");
        author.setAge(45);
        book.setAuthor(author);
        return book;
    }

    @Test
    public void testSerializeAndDeserialize(){
        Book book = createBook();
        System.out.println("origin       book:" + book);

        byte[] bookData = ProtostuffSerializeUtil.serialize(book);

        Book deserializedBook = ProtostuffSerializeUtil.deserialize(bookData, Book.class);
        System.out.println("deserialized book:" + deserializedBook);
    }

}
