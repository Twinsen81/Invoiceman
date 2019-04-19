[![License Apache 2.0](https://img.shields.io/badge/License-Apache%202.0-blue.svg?style=true)](http://www.apache.org/licenses/LICENSE-2.0)
![minSdkVersion 23](https://img.shields.io/badge/minSdkVersion-21-red.svg?style=true)
![compileSdkVersion 28](https://img.shields.io/badge/compileSdkVersion-28-blueviolet.svg?style=true)
[![CircleCI](https://circleci.com/gh/Twinsen81/Invoiceman.svg?style=svg)](https://circleci.com/gh/Twinsen81/Invoiceman)

<img alt="Icon" src="app/src/main/res/mipmap-xxhdpi/ic_launcher2.png?raw=true" align="left" hspace="1" vspace="1">

<a alt='Try it on Google Play' href='https://play.google.com/store/apps/details?id=com.evartem.invoiceman' target='_blank' align='right'><img
align='right' height='36' style='border:0px;height:36px;' src='https://developer.android.com/images/brand/en_generic_rgb_wo_60.png' border='0' /></a>
# INVOICEMAN

A pet project for processing invoices at a storage facility (warehouse). The app is not finished yet. The backend doesn't exist yet,
so it is simulated within the app.   
<BR/>
<p align="center">
  <img alt='Sample screenshots' src="/art/sampleshots.jpg">
</p>

#### The Workflow
The app is designed to be used in the following workflow:
1. An employee at the office receives an invoice from a supplier;
2. The employee enters data from the invoice into the backend's DB using a GUI app or a web frontend;
3. The supplier ships products listed in the invoice to the company's warehouse;
4. An employee at the warehouse finds the corresponding invoice in the app and accepts it for processing;
5. He or she scans or enters manually serial numbers (possibly, some other info) of the products using the app, then submits the collected data 
to the backend;
6. An employee at the office reviews the data and confirms the successful receipt of the products;
7. The data is kept on the backend for future reference (e.g. warranty service requests).   

## The Architecture

The app is built using the [clean architecture approach](https://www.youtube.com/watch?v=Nsjsiz2A9mg). The following diagram 
depicts the objects and relationships used in this implementation of the clean architecture:

<p align="center">
  <img alt='Clean architecture implementation' src="/art/Invoiceman_clean_architecture.png">
</p>
 
 The UI layer is implemented using the MVI pattern. Although there're many existing libraries, I've decided to implement it
 myself to get better understanding on how it works. The result is far from ideal but it works as intended. The implementation
 was inspired by:
 * [The State of Managing State with RxJava by Jake Wharton](https://jakewharton.com/the-state-of-managing-state-with-rxjava/)
 * [Reactive Apps with Model-View-Intent by Hannes Dorfmann](http://hannesdorfmann.com/android/mosby3-mvi-1)
 * [Kaushik Gopal's approach](https://fragmentedpodcast.com/episodes/148/)
<BR/>
 The gist of the implementation is depicted on the following diagram:
<p align="center">
  <img alt='MVI implementation' src="/art/invoiceman_mvi.png">
</p>

## Credits

* Frameworks, libraries & tools: Koin, Moshi, Retrofit, Okhttp, RxJava2 + RxKotlin, RxBinding, FastAdapter, Glide,
zxing-android-embedded ,leinardi.android:speed-dial, Timber, LeakCanary, Mockito, RESTMock, detekt
* Free logo by [LogoMakr.com](https://logomakr.com)
* Diagrams by [Lucidchart.com](https://www.lucidchart.com)
* Screenshots by [theapplaunchpad.com](https://theapplaunchpad.com)

## License

    Copyright 2019 Evgeniy Plokhov

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
