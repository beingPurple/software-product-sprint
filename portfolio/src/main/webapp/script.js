// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/**
 * Adds a random greeting to the page.
 */

function randMessage() {
    const msgList =
        ['“With a successful attack roll, the wizard maintains the thief in surfboard position.”',
         'DM: So, what are your character’s flaws? Player: Acid reflux.',
          'That’s too indiscriminate.  We should go with the rampaging bear.',
          'We’ve managed to teach the robot about the most important human emotion: Humiliation.',
          '“Do you have a permit for that facial hair?”',
          'Cleric: “Everybody chill, it’s totes God’s will.”',
          'Did asterisks exist in 2006?'];
    const msg = msgList[Math.floor(Math.random() * msgList.length)];
    const msgContainer = document.getElementById('message-container');
    msgContainer.innerText = msg;
}
async function servlet(){
    const response = await fetch('/data');
  const quote = await response.text();
  document.getElementById('quote-container').innerText = quote;
}

function addRandomGreeting() {
    const greetings =
        ['Hello world!', '¡Hola Mundo!', '你好，世界！', 'Bonjour le monde!'];

    // Pick a random greeting.
    const greeting = greetings[Math.floor(Math.random() * greetings.length)];

    // Add it to the page.
    const greetingContainer = document.getElementById('greeting-container');
    greetingContainer.innerText = greeting;
}
