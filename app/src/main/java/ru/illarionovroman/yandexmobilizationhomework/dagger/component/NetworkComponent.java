package ru.illarionovroman.yandexmobilizationhomework.dagger.component;


import javax.inject.Singleton;

import dagger.Component;
import ru.illarionovroman.yandexmobilizationhomework.dagger.module.NetworkModule;
import ru.illarionovroman.yandexmobilizationhomework.ui.fragment.translation.TranslationFragment;

@Singleton
@Component(modules = NetworkModule.class)
public interface NetworkComponent {
    void inject(TranslationFragment fragment);
}
