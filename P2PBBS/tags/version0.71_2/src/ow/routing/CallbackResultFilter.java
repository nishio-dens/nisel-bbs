/*
 * Copyright 2006,2008 National Institute of Advanced Industrial Science
 * and Technology (AIST), and contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ow.routing;

import java.io.Serializable;

/**
 * A filter which determines whether a value returned by {@link ow.routing.CallbackOnRoute#process(ow.id.ID, int, Serializable[], ow.id.IDAddressPair, boolean) CallbackOnRoute#process()}
 * is passed back to the requester or not.
 */
public interface CallbackResultFilter extends Serializable {
	Serializable filter(Serializable result);
}
