/*
 * Copyright 2000-2016 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.ui;

import java.io.Serializable;
import java.util.EventObject;
import java.util.Optional;

import elemental.json.JsonValue;

/**
 * Represents <code>window.history</code> in the browser. See e.g.
 * <a href="https://developer.mozilla.org/en-US/docs/Web/API/History_API">
 * documentation on MDN</a> for detailed information on how the API works in the
 * browser.
 *
 * @since
 * @author Vaadin Ltd
 */
public class History implements Serializable {

    /**
     * Event fired when the a <code>PopStateEvent</code> is fired in the
     * browser.
     */
    public static class PopStateEvent extends EventObject {
        private final String location;
        private final transient JsonValue state;

        /**
         * Creates a new event.
         *
         * @param history
         *            the history instance that fired the event, not
         *            <code>null</code>
         * @param state
         *            the history state from the browser, <code>null</code> if
         *            no state was provided
         * @param location
         *            the new browser location, not <code>null</code>
         */
        public PopStateEvent(History history, JsonValue state,
                String location) {
            super(history);

            assert location != null;

            this.location = location;
            this.state = state;
        }

        @Override
        public History getSource() {
            return (History) super.getSource();
        }

        /**
         * Gets the location from the browser.
         *
         * @return the location, not null
         */
        public String getLocation() {
            return location;
        }

        /**
         * Gets the history state value as JSON.
         *
         * @return an optional JSON state value
         */
        public Optional<JsonValue> getState() {
            return Optional.ofNullable(state);
        }
    }

    /**
     * Handles <code>popstate</code> events from the browser.
     */
    @FunctionalInterface
    public interface PopStateHandler extends Serializable {
        /**
         * Invoked when a <code>popstate</code> event is fired.
         *
         * @param event
         *            the event
         */
        void onPopState(PopStateEvent event);
    }

    private final UI ui;
    private PopStateHandler popStateHandler;

    /**
     * Creates a history API endpoint for the given UI.
     *
     * @param ui
     *            the ui, not null
     */
    public History(UI ui) {
        assert ui != null;
        this.ui = ui;
    }

    /**
     * Gets the UI that this instance belongs to.
     *
     * @return the ui, not null
     */
    public UI getUI() {
        return ui;
    }

    /**
     * Invokes <code>history.pushState</code> in the browser with the given
     * parameters.
     *
     * @param state
     *            the JSON state to push to the history stack, or
     *            <code>null</code> to only change the location
     * @param location
     *            the new location to set in the browser, or <code>null</code>
     *            to only change the JSON state
     */
    public void pushState(JsonValue state, String location) {
        // Second parameter is title which is currently ignored according to
        // https://developer.mozilla.org/en-US/docs/Web/API/History_API
        ui.getPage().executeJavaScript("history.pushState($0, '', $1)", state,
                location);
    }

    /**
     * Invokes <code>history.replaceState</code> in the browser with the given
     * parameters.
     *
     * @param state
     *            the JSON state to push to the history stack, or
     *            <code>null</code> to only change the location
     * @param location
     *            the new location to set in the browser, or <code>null</code>
     *            to only change the JSON state
     */
    public void replaceState(JsonValue state, String location) {
        // Second parameter is title which is currently ignored according to
        // https://developer.mozilla.org/en-US/docs/Web/API/History_API
        ui.getPage().executeJavaScript("history.replaceState($0, '', $1)",
                state, location);
    }

    /**
     * Sets a handler that will be notified when a <code>popstate</code> event
     * is fired in the browser. There can only be one handler at a time.
     *
     * @param popStateHandler
     *            the handler to set, or <code>null</code> to remove the current
     *            handler
     */
    public void setPopStateHandler(PopStateHandler popStateHandler) {
        this.popStateHandler = popStateHandler;
    }

    /**
     * Gets the handler that is notified when a <code>popstate</code> event is
     * fired in the browser.
     *
     * @return the popstate handler, or <code>null</code> if no handler is set
     */
    public PopStateHandler getPopStateHandler() {
        return popStateHandler;
    }

    /**
     * Navigates back. This has the same effect as if the user would press the
     * back button in the browser. This causes a {@link PopStateEvent} to be
     * fired asynchronously if the conditions described in the <a href=
     * "https://developer.mozilla.org/en-US/docs/Web/API/WindowEventHandlers/onpopstate">
     * onpopstate documentation</a> are met.
     */
    public void back() {
        ui.getPage().executeJavaScript("history.back()");
    }

    /**
     * Navigates forward. This has the same effect as if the user would press
     * the forward button in the browser. This causes a {@link PopStateEvent} to
     * be fired asynchronously if the conditions described in the <a href=
     * "https://developer.mozilla.org/en-US/docs/Web/API/WindowEventHandlers/onpopstate">
     * onpopstate documentation</a> are met.
     */
    public void forward() {
        ui.getPage().executeJavaScript("history.forward()");
    }

    /**
     * Navigates a number of steps forward or backward in the history. This has
     * the same effect as if the user would press the forward button in the
     * browser.This causes a {@link PopStateEvent} to be fired asynchronously if
     * the conditions described in the <a href=
     * "https://developer.mozilla.org/en-US/docs/Web/API/WindowEventHandlers/onpopstate">
     * onpopstate documentation</a> are met.
     *
     * @param steps
     *            the number of steps to navigate, positive numbers navigate
     *            forward, negative numbers backward. <code>0</code> causes the
     *            current page to be reloaded
     */
    public void go(int steps) {
        ui.getPage().executeJavaScript("history.go($0)",
                Integer.valueOf(steps));
    }
}