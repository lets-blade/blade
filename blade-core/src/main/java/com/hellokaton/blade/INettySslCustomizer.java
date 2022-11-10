/*
  Copyright (c) 2022, katon (hellokaton@gmail.com)
  <p>
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
  <p>
  http://www.apache.org/licenses/LICENSE-2.0
  <p>
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */
package com.hellokaton.blade;

import io.netty.handler.ssl.SslContext;

/**
 * To customize TLS implementation for the Netty Server
 * 
 * @author JFLOURNO
 * 
 */
public interface INettySslCustomizer {
	
	/**
	 * Create a custom SslContext that will be used for the Netty server.
	 * 
	 * @return SslContext to be used.
	 */
	public SslContext getCustomSslContext(Blade blade);
}
