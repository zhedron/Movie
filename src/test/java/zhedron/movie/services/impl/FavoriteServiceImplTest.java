package zhedron.movie.services.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zhedron.movie.dto.response.FavoriteResponse;
import zhedron.movie.dto.response.MediaContentResponse;
import zhedron.movie.dto.response.request.FavoriteRequest;
import zhedron.movie.entity.Favorite;
import zhedron.movie.entity.MediaContent;
import zhedron.movie.entity.User;
import zhedron.movie.exceptions.FavoriteNotFoundException;
import zhedron.movie.exceptions.FavoriteExistException;
import zhedron.movie.exceptions.MediaContentExistInFavoriteException;
import zhedron.movie.exceptions.MediaContentNotExistInFavoriteException;
import zhedron.movie.mappers.FavoriteMapper;
import zhedron.movie.mappers.MediaContentMapper;
import zhedron.movie.repository.FavoriteRepository;
import zhedron.movie.repository.MediaContentRepository;
import zhedron.movie.repository.UserRepository;
import zhedron.movie.services.MediaContentService;
import zhedron.movie.services.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FavoriteServiceImplTest {
    @Mock
    private UserService userService;
    @Mock
    private MediaContentService mediaContentService;
    @Mock
    private FavoriteRepository favoriteRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private MediaContentRepository mediaContentRepository;
    @Mock
    private FavoriteMapper favoriteMapper;
    @Mock
    private MediaContentMapper mediaContentMapper;
    @InjectMocks
    private FavoriteServiceImpl favoriteService;

    @Test
    void createSavesFavoriteAndAttachesItToCurrentUser() {
        FavoriteRequest request = request("Watch later");
        Favorite saved = favorite(4L, "Watch later");
        User user = userWithFavorites();
        FavoriteResponse expected = response(4L, "Watch later");
        when(userService.getCurrentUser()).thenReturn(user);
        when(favoriteRepository.existsByUserIdAndName(0L, "Watch later")).thenReturn(false);
        when(favoriteRepository.save(any(Favorite.class))).thenReturn(saved);
        when(favoriteMapper.toFavoriteResponse(saved)).thenReturn(expected);

        FavoriteResponse actual = favoriteService.create(request);

        ArgumentCaptor<Favorite> captor = ArgumentCaptor.forClass(Favorite.class);
        verify(favoriteRepository).save(captor.capture());
        assertEquals("Watch later", captor.getValue().getName());
        assertSame(user, captor.getValue().getUser());
        assertSame(expected, actual);
    }

    @Test
    void createRejectsDuplicateFavoriteNameForCurrentUser() {
        User user = userWithFavorites();
        user.setId(7L);
        when(userService.getCurrentUser()).thenReturn(user);
        when(favoriteRepository.existsByUserIdAndName(7L, "Watch later")).thenReturn(true);

        assertThrows(FavoriteExistException.class, () -> favoriteService.create(request("Watch later")));

        verify(favoriteRepository, never()).save(any(Favorite.class));
    }

    @Test
    void getFavoriteByNameReturnsCurrentUsersFavorite() {
        User user = userWithFavorites();
        user.setId(7L);
        Favorite favorite = favorite(4L, "Watch later");
        FavoriteResponse expected = response(4L, "Watch later");
        when(userService.getCurrentUser()).thenReturn(user);
        when(favoriteRepository.findByUserIdAndName(7L, "Watch later")).thenReturn(Optional.of(favorite));
        when(favoriteMapper.toFavoriteResponse(favorite)).thenReturn(expected);

        FavoriteResponse actual = favoriteService.getFavoriteByName("Watch later");

        assertSame(expected, actual);
        verify(favoriteRepository).findByUserIdAndName(7L, "Watch later");
    }

    @Test
    void getFavoriteByNameThrowsWhenCurrentUserDoesNotHaveFavorite() {
        User user = userWithFavorites();
        user.setId(7L);
        when(userService.getCurrentUser()).thenReturn(user);
        when(favoriteRepository.findByUserIdAndName(7L, "Missing")).thenReturn(Optional.empty());

        assertThrows(FavoriteNotFoundException.class, () -> favoriteService.getFavoriteByName("Missing"));
    }

    @Test
    void updateChangesNameAndMapsSavedFavorite() {
        Favorite favorite = favorite(4L, "Old name");
        User user = userWithFavorites(favorite);
        FavoriteResponse expected = response(4L, "New name");
        when(userService.getCurrentUser()).thenReturn(user);
        when(favoriteRepository.findById(4L)).thenReturn(Optional.of(favorite));
        when(favoriteRepository.save(favorite)).thenReturn(favorite);
        when(favoriteMapper.toFavoriteResponse(favorite)).thenReturn(expected);

        FavoriteResponse actual = favoriteService.update(request("New name"), 4L);

        assertEquals("New name", favorite.getName());
        assertSame(expected, actual);
    }

    @Test
    void updateRejectsFavoriteNotOwnedByCurrentUser() {
        Favorite favorite = favorite(4L, "Old name");
        when(userService.getCurrentUser()).thenReturn(userWithFavorites());
        when(favoriteRepository.findById(4L)).thenReturn(Optional.of(favorite));

        assertThrows(FavoriteNotFoundException.class,
                () -> favoriteService.update(request("New name"), 4L));

        verify(favoriteRepository, never()).save(any(Favorite.class));
    }

    @Test
    void findByIdThrowsWhenFavoriteDoesNotExist() {
        when(favoriteRepository.findById(404L)).thenReturn(Optional.empty());

        assertThrows(FavoriteNotFoundException.class, () -> favoriteService.findById(404L));
    }

    @Test
    void deleteRemovesOwnedFavoriteAndDeletesIt() {
        Favorite favorite = favorite(4L, "Watch later");
        User user = userWithFavorites(favorite);
        when(userService.getCurrentUser()).thenReturn(user);
        when(favoriteRepository.findById(4L)).thenReturn(Optional.of(favorite));

        favoriteService.deleteFavorite(4L);

        assertEquals(List.of(), user.getFavorites());
        verify(favoriteRepository).delete(favorite);
    }

    @Test
    void deleteRejectsFavoriteOwnedByAnotherUser() {
        Favorite stored = favorite(4L, "Watch later");
        User currentUser = userWithFavorites();
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(favoriteRepository.findById(4L)).thenReturn(Optional.of(stored));

        assertThrows(FavoriteNotFoundException.class, () -> favoriteService.deleteFavorite(4L));

        verify(favoriteRepository, never()).delete(any(Favorite.class));
    }

    @Test
    void addMediaContentSavesOwnedFavoriteWithMedia() {
        Favorite favorite = favorite(4L, "Watch later");
        User user = userWithFavorites(favorite);
        MediaContent media = mediaContent(9L);
        FavoriteResponse expected = response(4L, "Watch later");
        when(userService.getCurrentUser()).thenReturn(user);
        when(favoriteRepository.findById(4L)).thenReturn(Optional.of(favorite));
        when(mediaContentRepository.findById(9L)).thenReturn(Optional.of(media));
        when(favoriteRepository.save(favorite)).thenReturn(favorite);
        when(favoriteMapper.toFavoriteResponse(favorite)).thenReturn(expected);

        FavoriteResponse actual = favoriteService.addMediaContentToFavorite(4L, 9L);

        assertEquals(List.of(media), favorite.getMediaContents());
        verify(favoriteRepository).save(favorite);
        assertSame(expected, actual);
    }

    @Test
    void addMediaContentRejectsDuplicateMedia() {
        Favorite favorite = favorite(4L, "Watch later");
        MediaContent media = mediaContent(9L);
        favorite.setMediaContents(new ArrayList<>(List.of(media)));
        when(userService.getCurrentUser()).thenReturn(userWithFavorites(favorite));
        when(favoriteRepository.findById(4L)).thenReturn(Optional.of(favorite));
        when(mediaContentRepository.findById(9L)).thenReturn(Optional.of(media));

        assertThrows(MediaContentExistInFavoriteException.class,
                () -> favoriteService.addMediaContentToFavorite(4L, 9L));

        verify(favoriteRepository, never()).save(any(Favorite.class));
    }

    @Test
    void removeMediaContentSavesFavoriteWithoutMedia() {
        Favorite favorite = favorite(4L, "Watch later");
        MediaContent media = mediaContent(9L);
        favorite.setMediaContents(new ArrayList<>(List.of(media)));
        FavoriteResponse expected = response(4L, "Watch later");
        when(userService.getCurrentUser()).thenReturn(userWithFavorites(favorite));
        when(favoriteRepository.findById(4L)).thenReturn(Optional.of(favorite));
        when(mediaContentRepository.findById(9L)).thenReturn(Optional.of(media));
        when(favoriteRepository.save(favorite)).thenReturn(favorite);
        when(favoriteMapper.toFavoriteResponse(favorite)).thenReturn(expected);

        FavoriteResponse actual = favoriteService.removeMediaContentFromFavorite(4L, 9L);

        assertEquals(List.of(), favorite.getMediaContents());
        assertSame(expected, actual);
    }

    @Test
    void removeMediaContentRejectsUnlinkedMedia() {
        Favorite favorite = favorite(4L, "Watch later");
        favorite.setMediaContents(new ArrayList<>());
        when(userService.getCurrentUser()).thenReturn(userWithFavorites(favorite));
        when(favoriteRepository.findById(4L)).thenReturn(Optional.of(favorite));
        when(mediaContentRepository.findById(9L)).thenReturn(Optional.of(mediaContent(9L)));

        assertThrows(MediaContentNotExistInFavoriteException.class,
                () -> favoriteService.removeMediaContentFromFavorite(4L, 9L));
    }

    private static FavoriteRequest request(String name) {
        FavoriteRequest request = new FavoriteRequest();
        request.setName(name);
        return request;
    }

    private static Favorite favorite(long id, String name) {
        Favorite favorite = new Favorite();
        favorite.setId(id);
        favorite.setName(name);
        favorite.setMediaContents(new ArrayList<>());
        return favorite;
    }

    private static User userWithFavorites(Favorite... favorites) {
        User user = new User();
        user.setFavorites(new ArrayList<>(List.of(favorites)));
        return user;
    }

    private static MediaContent mediaContent(long id) {
        MediaContent media = new MediaContent();
        media.setId(id);
        return media;
    }

    private static FavoriteResponse response(long id, String name) {
        return new FavoriteResponse(id, name, List.<MediaContentResponse>of(), null);
    }
}
