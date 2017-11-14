(ns memory-hole.handlers
  (:require [re-frame.core :refer [dispatch dispatch-sync reg-event-db reg-event-fx]]
            [ajax.core :refer [DELETE GET POST PUT]]
            [memory-hole.db :as db]
            memory-hole.handlers.admin
            memory-hole.handlers.groups
            memory-hole.handlers.issues
            memory-hole.handlers.tags))

(reg-event-db
  :initialize-db
  (fn [_ _]
    db/default-db))

(reg-event-db
  :set-active-page
  (fn [db [_ page]]
    (assoc db :active-page page)))

(reg-event-fx
  :run-login-events
  (fn [{{events :login-events :as db} :db} _]
    {:dispatch-n events
     :db db}))

(reg-event-db
  :add-login-event
  (fn [db [_ event]]
    (update db :login-events conj event)))

(reg-event-db
  :login
  (fn [db [_ user]]
    (dispatch [:run-login-events])
    (assoc db :user user)))

(reg-event-db
  :logout
  (fn [db _]
    (dissoc db :user)))

(reg-event-db
  :set-error
  (fn [db [_ error]]
    (assoc db :error error)))

(reg-event-db
  :clear-error
  (fn [db _]
    (dissoc db :error)))

(reg-event-db
  :unset-loading
  (fn [db _]
    (dissoc db :loading? :error :should-be-loading?)))

(reg-event-db
 :set-loading-for-real-this-time
 (fn [{:keys [should-be-loading?] :as db} _]
   (if should-be-loading?
     (assoc db :loading? true)
     db)))
;; Why error false, not dissoc error?
(reg-event-fx
  :set-loading
  (fn [{db :db} _]
    {:dispatch-later [{:ms 100 :dispatch [:set-loading-for-real-this-time]}]
     :db (-> db
            (assoc :should-be-loading? true)
            (dissoc :error))}))


